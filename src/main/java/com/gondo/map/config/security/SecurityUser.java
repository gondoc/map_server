package com.gondo.map.config.security;

import com.gondo.map.domain.member.record.SecurityMemberRecord;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
public class SecurityUser extends User {

    private final SecurityMemberRecord securityMemberRecord;

    public SecurityUser(SecurityMemberRecord memberRecord) {
        super(memberRecord.email(), memberRecord.nick(), AuthorityUtils.createAuthorityList(memberRecord.role()));
        this.securityMemberRecord = memberRecord;
    }

    @Override
    public String getPassword() {
        return null;
    }

}
