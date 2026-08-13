package com.schoolmanagement.iam.domain;

import java.util.Objects;

import com.schoolmanagement.iam.domain.exception.UserAlreadyDisabled;

public final class User {
    private final UserId id;
    private final Username username;
    private PasswordHash passwordHash;
    private final Role role;
    private final PersonId personId;
    private boolean enabled;

    private User(UserId id,
            Username username,
            PasswordHash passwordHash,
            Role role,
            PersonId personId,
            boolean enabled) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.personId = personId;
        this.enabled = enabled;
    }

    public static User register(UserId id,
            Username username,
            PasswordHash passwordHash,
            Role role,
            PersonId personId) {
        return new User(id, username, passwordHash, role, personId, true);
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void disable() {
        if (!enabled) {
            throw new UserAlreadyDisabled(id);
        }
        this.enabled = false;
    }

    public static User reconstitute(UserId id,
            Username username,
            PasswordHash passwordHash,
            Role role,
            PersonId personId,
            boolean enabled) {
        return new User(id, username, passwordHash, role, personId, enabled);
    }

    public UserId id() {
        return id;
    }

    public Username username() {
        return username;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public Role role() {
        return role;
    }

    public PersonId personId() {
        return personId;
    }

    public boolean enabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
