package app.dto;

import java.util.Date;

public class UserDTO {
    private final Long id;
    private final String email;
    private final boolean accountLocked;
    private final Date createdAt;
    private final Date updatedAt;


    public UserDTO(Long id, String email, boolean accountLocked, Date createdAt) {
        this.id = id;
        this.email = email;
        this.accountLocked = accountLocked;
        this.createdAt = createdAt;
        this.updatedAt = new Date();
    }



    public Long getId() { return id; }
    public String getEmail() { return email; }
    public boolean getAccountLocked(){ return  accountLocked; }
    public Date getCreatedAt() {return createdAt;}
    public Date getUpdatedAt() {return updatedAt;}
}