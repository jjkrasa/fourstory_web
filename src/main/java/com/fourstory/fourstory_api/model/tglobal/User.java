package com.fourstory.fourstory_api.model.tglobal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "TACCOUNT")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "dwUserID")
    private Integer id;

    @Column(name = "szUserID", length = 50, nullable = false, unique = true)
    private String szUserID;

    @Column(name = "szPasswd", length = 50, nullable = false)
    private String password;

    @Column(name = "bCheck")
    private Byte checkFlag;

    @Column(name = "dFirstLogin")
    private LocalDateTime firstLogin;

    @Column(name = "dLastLogin")
    private LocalDateTime lastLogin;
}
