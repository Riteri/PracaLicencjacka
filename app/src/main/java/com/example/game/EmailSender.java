package com.example.game;

import android.util.Log;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL = "gavnuq321@gmail.com";
    private static final String PASSWORD = "ozzbbanellfbsmgu";

    public static void sendEmail(final String toEmail, final String subject, final String body) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", SMTP_SERVER);
                props.put("mail.smtp.port", SMTP_PORT);

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(EMAIL, PASSWORD);
                            }
                        });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(EMAIL));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(toEmail));
                    message.setSubject(subject);
                    message.setText(body);

                    Transport.send(message);
                    Log.d("EmailSender", "Email sent successfully to " + toEmail + " with subject " + subject + " and body " + body);

                } catch (MessagingException e) {
                    throw new RuntimeException(e);

                }
            }
        });

        thread.start();
    }
}
