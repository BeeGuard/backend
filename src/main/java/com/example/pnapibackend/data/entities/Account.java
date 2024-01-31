package com.example.pnapibackend.data.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @NonNull
    @Column(unique = true, name = "email")
    private String email;

    @NonNull
    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @NonNull
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Hive> hives;

    private void setId(){
        //do nothing
    }

    public boolean equals(Object object) {
        if (object instanceof Account account) {
            return account.getEmail().equals(this.getEmail());
        }
        return false;
    }
}
