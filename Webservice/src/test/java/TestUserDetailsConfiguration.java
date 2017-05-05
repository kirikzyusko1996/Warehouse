import com.itechart.warehouse.constants.UserRoleEnum;
import com.itechart.warehouse.entity.Role;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import com.itechart.warehouse.security.WarehouseCompanyUserDetails;
import com.itechart.warehouse.security.WarehouseCompanyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * User details and user details service config bean for tests.
 */
@Configuration
public class TestUserDetailsConfiguration {

    public UserDetails userDetails() {
        User user = new User();
        user.setId(Long.valueOf(1));
        user.setLogin("1");
        user.setPassword("1");
        ArrayList<Role> roles = new ArrayList<>();
        Role roleSupervisor = new Role();
        roleSupervisor.setRole(UserRoleEnum.ROLE_SUPERVISOR.toString());
        Role roleDispatcher = new Role();
        roleDispatcher.setRole(UserRoleEnum.ROLE_DISPATCHER.toString());
        Role roleAdmin = new Role();
        roleAdmin.setRole(UserRoleEnum.ROLE_ADMIN.toString());
        Role roleController = new Role();
        roleController.setRole(UserRoleEnum.ROLE_CONTROLLER.toString());
        Role roleManager = new Role();
        roleManager.setRole(UserRoleEnum.ROLE_MANAGER.toString());
        Role roleOwner = new Role();
        roleOwner.setRole(UserRoleEnum.ROLE_OWNER.toString());
        roles.add(roleSupervisor);
        roles.add(roleDispatcher);
        roles.add(roleAdmin);
        roles.add(roleController);
        roles.add(roleManager);
        roles.add(roleOwner);

        WarehouseCompany company = new WarehouseCompany();
        company.setIdWarehouseCompany(Long.valueOf(10));
        user.setWarehouseCompany(company);
        user.setRoles(roles);
        return new WarehouseCompanyUserDetails(user);
    }

    @Bean
    public UserDetailsService userDetailsService(){
        WarehouseCompanyUserDetailsService userDetailsService = new WarehouseCompanyUserDetailsService(){
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userDetails();
            }
        };

        return userDetailsService;
    }
}
