package com.veersa.training.smartcontactmanager.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.Properties;
import javax.mail.internet.InternetAddress;
import  javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    public boolean sendEmail(String subject,String message,String to){

        boolean f = false;
        String from = "sachinyadav0278@gmail.com";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES "+properties);

        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("sachinyadav0278@gmail.com","oskd zkbj hyns zfok");
            }
        });





        session.setDebug(true);
        MimeMessage m = new MimeMessage(session);

        try{
            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
//            m.setText(message);
            m.setContent(message,"text/html");
            Transport.send(m);
            System.out.println("Sent success...");
            f = true;

        }
        catch(Exception e ){
            e.printStackTrace();
        }
        return f;
    }
}
