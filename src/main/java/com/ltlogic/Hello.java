package com.ltlogic;

import com.ltlogic.db.repository.UserRepository;
import okhttp3.Response;
import java.io.IOException;
import javax.persistence.EntityManager;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Hello {

    @Autowired
    public UserRepository u;
    @Autowired
    EntityManager en;

    public static void main(String[] args) throws IOException {
        HttpUrl url = HttpClient.httpUrlBuilder("tournaments/omsttt21/participants.json");
        String response = HttpClient.makeGetRestCall(url);
        System.out.println("Response: " + response);
    }

}
