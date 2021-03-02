package org.resistance.site.web;

import static org.resistance.site.web.utils.ShabaJdbcUserDetailsManager.ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.resistance.site.web.utils.ShabaJdbcUserDetailsManager;
import org.resistance.site.web.utils.ShabaUser;
import org.resistance.site.web.utils.SignUpFormValidator;
import org.resistance.site.web.utils.UserForm;
import org.resistance.site.web.utils.UserTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/** @author Alex Aiezza */
@Controller
public class UserController {

  private static final String UNFINISHED_SIGN_UP_FORM = "_unifinshedSignUpForm_";

  private final Log LOGGER;

  private final ShabaJdbcUserDetailsManager USER_MAN;

  private final UserTracker USER_TRACKER;

  @Autowired
  public UserController(
      @Qualifier("User_Logger") Log logger,
      ShabaJdbcUserDetailsManager userMan,
      UserTracker userTracker) {
    LOGGER = logger;
    USER_MAN = userMan;
    USER_TRACKER = userTracker;
  }

  @RequestMapping(method = POST, value = "retrieveUsers")
  @ResponseBody
  public Collection<ShabaUser> getUsers() throws SQLException, IllegalAccessException {
    return USER_MAN.getUsers();
  }

  @RequestMapping(method = GET, value = "userManagement")
  public ModelAndView getUserManagementPage() throws SQLException {
    return new ModelAndView("userManagement");
  }

  @RequestMapping(method = GET, value = "whoAmI")
  @ResponseBody
  public String getCurrentUser() {
    return USER_MAN.getShabaUser().getUsername();
  }

  @RequestMapping(method = GET, value = "/signup")
  public ModelAndView getSignupPage() {
    if (USER_TRACKER.contains(USER_MAN.getShabaUser())) {
      return new ModelAndView("redirect:profile");
    }
    return new ModelAndView("signup");
  }

  @ModelAttribute("newUserForm")
  public UserForm populateNewUserForm(UserForm user, HttpSession session) {
    UserForm form = (UserForm) session.getAttribute(UNFINISHED_SIGN_UP_FORM);

    if (!user.isBlank()) {
      user.clearPassword();
      return user;
    }

    if (form != null) {
      form.clearPassword();
      return form;
    }

    return new UserForm();
  }

  @RequestMapping(method = POST, value = "getUser/{someusername}")
  @ResponseBody
  public HashMap<String, Object> getUser(@PathVariable("someusername") String username)
      throws SQLException, UnsupportedEncodingException {
    if (!USER_MAN.userExists(username)) {
      return null;
    }
    HashMap<String, Object> json = new HashMap<String, Object>();

    json.put("user", USER_MAN.loadShabaUserByUsername(username));

    ShabaUser currentUser = USER_MAN.getShabaUser();

    boolean admin = currentUser.getAuthorities().contains(ADMIN);

    json.put("isAdmin", admin);

    return json;
  }

  @RequestMapping(method = POST, value = "signup")
  public ModelAndView signup(UserForm form, BindingResult result, HttpServletRequest request) {
    // VALIDATE THE FORM
    SignUpFormValidator suValidator = new SignUpFormValidator();

    if (!form.isBlank()) {
      request.getSession().setAttribute(UNFINISHED_SIGN_UP_FORM, form);
    }

    suValidator.validate(form, result);

    if (USER_MAN.userExists(form.getUsername())) {
      result.rejectValue("username", "Username is already taken");
    }

    // PRINT OUT ALL ERRORS IF ANY
    if (result.hasErrors()) {
      StringBuilder out = new StringBuilder();

      for (ObjectError er : result.getAllErrors()) {
        out.append(er.getCode()).append("<br>");
      }

      return new ModelAndView("signup", "message", out);
    }

    // IF NO ERRORS, CREATE AND AUTHENTICATE USER
    String message = createNewUser(form);

    if (message != null) {
      return new ModelAndView("signup", "message", message);
    }

    LOGGER.info(String.format("FRESH MEAT -> %s", form));

    return new ModelAndView("redirect:login", "message", message);
  }

  private String createNewUser(UserForm user) {
    // ADD new user to database
    try {
      USER_MAN.createUser(user);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return e.getMessage();
    }

    return null;
  }

  @RequestMapping(method = POST, value = "deleteUser/{someusername}")
  private void deleteUser(
      @PathVariable("someusername") String username, HttpServletResponse response)
      throws UnsupportedEncodingException {
    USER_MAN.deleteUser(username);

    LOGGER.info(String.format("USER :%s: has been DELETED! ", username));

    response.setStatus(HttpStatus.OK.value());
  }

  @RequestMapping(method = GET, value = "userDetails/{user}")
  private ModelAndView userDetails(
      @PathVariable String user, @RequestParam(required = false) String message)
      throws UnsupportedEncodingException {
    HashMap<String, Object> map = new HashMap<String, Object>();

    ShabaUser currentUser = USER_MAN.getShabaUser();

    boolean admin = currentUser.getAuthorities().contains(ADMIN);

    if (!USER_MAN.userExists(user) || (!currentUser.equals(user) && !admin)) {
      return new ModelAndView(String.format("redirect:%s", currentUser.getUsername()));
    }

    map.put("admin", admin);

    map.put("user", user);

    map.put("availableAuthorities", USER_MAN.getAvailableAuthorities());

    map.put("userAuths", USER_MAN.loadUserByUsername(user).getAuthorities());

    if (message != null) {
      map.put("message", message);
    }

    return new ModelAndView("userDetails", map);
  }

  @RequestMapping(method = POST, value = "updateUser")
  private ModelAndView updateUser(
      UserForm userToUpdate,
      @RequestParam(value = "auths") ArrayList<SimpleGrantedAuthority> authorities,
      BindingResult result) {
    userToUpdate.setAuthorities(authorities);

    if (!USER_MAN.userExists(userToUpdate.getUsername())) {
      result.rejectValue("username", "Username does not exist");
    }

    ShabaUser shabaUser = USER_MAN.loadShabaUserByUsername(userToUpdate.getUsername());

    SignUpFormValidator suValidator = new SignUpFormValidator();

    if (userToUpdate.getConfirmPassword().isEmpty()) {
      suValidator.validateRequiredFields(userToUpdate, result);

      if (!DigestUtils.sha1Hex(userToUpdate.getPassword()).equals(shabaUser.getPassword())
          && !USER_MAN.checkForAdminRights(USER_MAN.getShabaUser())) {
        result.rejectValue("password", "Wrong Password");
      }

    } else {
      suValidator.validate(userToUpdate, result);
    }

    // PRINT OUT ALL ERRORS IF ANY
    if (result.hasErrors()) {
      StringBuilder out = new StringBuilder();

      for (ObjectError er : result.getAllErrors()) {
        out.append(er.getCode()).append("<br>");
      }

      return new ModelAndView("redirect:userDetails/" + userToUpdate.getUsername(), "message", out);
    }

    if (userToUpdate.getConfirmPassword().isEmpty()) {
      USER_MAN.updateUser(ShabaUser.ShabaUserFromForm(userToUpdate));
    } else {
      USER_MAN.changePassword(
          userToUpdate.getUsername(), shabaUser.getPassword(), userToUpdate.getPassword());
    }

    String message =
        String.format("%s UPDATED!", USER_MAN.loadShabaUserByUsername(shabaUser.getUsername()));

    LOGGER.info(message);

    return new ModelAndView(
        "redirect:userDetails/" + userToUpdate.getUsername(), "message", message);
  }
}
