package com.groupstp.maintenance.core.security;

import com.groupstp.maintenance.config.MaintenanceConfig;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.auth.Credentials;
import com.haulmont.cuba.security.auth.checks.AbstractUserAccessChecker;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.cuba.security.global.LoginException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * Checks if login to system denied since server are in maintenance right now
 *
 * @author adiatullin
 */
@Component("mtnc_MaintenanceUserAccessChecker")
public class MaintenanceUserAccessChecker extends AbstractUserAccessChecker implements Ordered {

    @Inject
    protected MaintenanceConfig config;

    @Inject
    public MaintenanceUserAccessChecker(Messages messages) {
        super(messages);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PLATFORM_PRECEDENCE;
    }

    @Override
    public void check(Credentials credentials, AuthenticationDetails authenticationDetails) throws LoginException {
        User user = authenticationDetails.getSession().getCurrentOrSubstitutedUser();
        if (Boolean.TRUE.equals(config.getEnabled())) {
            if (!isAdmin(user)) {
                throw new LoginException(messages.getMessage(getClass(), "MaintenanceUserAccessChecker.serverUnderMaintenance"));
            }
        }
    }

    protected boolean isAdmin(User user) {
        if (user != null) {
            List<UserRole> userRoles = user.getUserRoles();
            if (!CollectionUtils.isEmpty(userRoles)) {
                for (UserRole ur : userRoles) {
                    if (RoleType.SUPER.equals(ur.getRole().getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
