package com.example.SpringBootTutorialProject.Service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String subject, String message,String to){
        //The variable f is used to track the success or failure of the email sending process.
        boolean f=false;
        //By initializing f to false, it indicates that the email sending process has not yet occurred or has failed.
        String from="arunneupane20@gmail.com";
        //Variable for Gmail
        String host="smtp.gmail.com";//represent the SMTP for gmail
        //get the system proporties
        Properties properties=System.getProperties();
        //setting
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable",true);
        properties.put("mail.smtp.auth",true);

        //step 1 to get session object
        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("arunneupane20@gmail.com","miweswcebhbckgyz");

            }
        });
        session.setDebug(true);
        MimeMessage m=new MimeMessage(session); //It creates a MimeMessage object using the session.
        try{
            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //adding subject to message
            m.setSubject(subject);
//            m.setText(message);
            m.setContent(message,"text/html");//the message will send through html or text formate
            Transport.send(m);//send the message using Transport class //The Transport.send() method is used to send the email message.
            f=false;
        }
        catch (Exception e){
            e.printStackTrace();

        }
        return f;
    }


}

