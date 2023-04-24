package com.capestone.capsproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Document
@Data
@NoArgsConstructor
public class UserFeed implements Serializable {
    @Id
    private String id;
    private String email;
    private String title;
    private String description;
    private String image;
    private ArrayList<String> tags;
    private String category;
    private boolean available=true;
    private String visibility;

    public UserFeed(String id, String email, String title, String description, String image, ArrayList<String> tags, String category, boolean available, String visibility) {
        this.id = id;
        this.email = email;

        this.title = title;
        this.description = description;
        this.image = image;
        this.tags = tags;
        this.category = category;
        this.available = available;
        this.visibility = visibility;
    }


}
