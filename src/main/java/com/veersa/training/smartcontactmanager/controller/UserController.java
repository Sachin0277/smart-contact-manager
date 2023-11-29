package com.veersa.training.smartcontactmanager.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.veersa.training.smartcontactmanager.entities.Contact;
import com.veersa.training.smartcontactmanager.entities.MyOrder;
import com.veersa.training.smartcontactmanager.entities.User;
import com.veersa.training.smartcontactmanager.helper.Message;
import com.veersa.training.smartcontactmanager.repositories.ContactRepository;
import com.veersa.training.smartcontactmanager.repositories.MyOrderRepository;
import com.veersa.training.smartcontactmanager.repositories.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MyOrderRepository myOrderRepository;


    @ModelAttribute
    public void commonDate(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("USERNAME " + userName);
        //Getting user by username

        User user = userRepository.getUserByUserName(userName);

        System.out.println("USER " + user);

        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String addContactForm(Model model) {
        model.addAttribute("title", "Add contact");
        model.addAttribute("contact", new Contact());
        return "normal/normal_add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            if (file.isEmpty()) {
                System.out.println("File is empty.");
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded.");
            }
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            System.out.println("Added to the database");
            // success message
            session.setAttribute("message", new Message("Your contact added !! Add more..", "success"));
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();
            //failure message
            session.setAttribute("message", new Message("Something went wrong! Try again ", "danger"));

        }
        return "normal/normal_add_contact_form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Show User Contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contact";
    }

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        System.out.println("CID " + cId);
        Optional<Contact> optionalContact = this.contactRepository.findById(cId);
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Contact contact = optionalContact.get();
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }

    /* Handler for deleting contact */
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session, Principal principal) {
        System.out.println("CID " + cId);
        Contact contact = this.contactRepository.findById(cId).get();
        User user = this.userRepository.getUserByUserName(principal.getName());
        user.getContacts().remove(contact);
        this.userRepository.save(user);
        System.out.println("CONTACT DELETED");
        session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));
        return "redirect:/user/show-contacts/0";
    }

    /* Handler for update form */
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cId, Model model) {
        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cId).get();
        model.addAttribute("contact", contact);
        return "normal/update-contact";
    }


    /* Handler for update contact */
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model m, HttpSession session, Principal principal) {
        try {
            Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
            if (!file.isEmpty()) {
                // delete old
                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldContact.getImage());
                file1.delete();

                //update new profile;
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            } else {
                contact.setImage(oldContact.getImage());
            }
            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Your Contact is Updated..", "success"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("CONTACT NAME " + contact.getName());
        System.out.println("CONTACT ID " + contact.getcId());


        return "redirect:/user/" + contact.getcId() + "/contact";
    }


    /* Handler for profile page showing */
    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile page");
        return "normal/profile";
    }

    /* open settings handler*/

    @GetMapping("/settings")
    public String openSettings() {
        return "normal/setting";
    }

    /* Change password handler*/
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
        System.out.println("OLD PASSWORD " + oldPassword);
        System.out.println("NEW PASSWORD " + newPassword);
        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());
        /* Matching old raw password and the current user password */
        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            //change the password
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Your Password Is Successfully Changed.", "success"));
        } else {
            //error -- message
            session.setAttribute("message", new Message("Please enter correct old password .", "danger"));
            return "redirect:/user/settings";

        }
        return "redirect:/user/index";
    }

    @PostMapping("/create-order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal) {
        System.out.println("Order function executed...");
        System.out.println(data);
        int amt = Integer.parseInt(data.get("amount").toString());
        try {
            RazorpayClient client = new RazorpayClient("rzp_test_GkzuBp2p3yGfe8", "2ERIpjYUehb2tl6y8XY8OBTq");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", amt * 100);
            jsonObject.put("currency", "INR");
            jsonObject.put("receipt", "txn_235425");


            Order order = client.Orders.create(jsonObject);
            System.out.println(order);
            /* Save order in the database */

            MyOrder myOrder = new MyOrder();
            myOrder.setAmount(order.get("amount") + "");
            myOrder.setOrderId(order.get("id"));
            myOrder.setPaymentId(null);
            myOrder.setStatus("created");
            myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
            myOrder.setReceipt(order.get("receipt"));

            this.myOrderRepository.save(myOrder);


            return order.toString();
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {

        MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
        myOrder.setPaymentId(data.get("payment_id").toString());
        myOrder.setStatus(data.get("status").toString());

        this.myOrderRepository.save(myOrder);
        System.out.println(data);

        return ResponseEntity.ok(Map.of("msg", "updated"));
    }
}
