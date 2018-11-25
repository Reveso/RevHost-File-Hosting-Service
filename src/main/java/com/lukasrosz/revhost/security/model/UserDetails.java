package com.lukasrosz.revhost.security.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="user_details")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class UserDetails {

    @Id
    @Column(name = "username")
    private String username;

    private boolean socialConnected;

}
