package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
public class UserController {

	private final UserService userService;
	private final RoleService roleService;

	@Autowired
	public UserController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	@GetMapping("/")
	public String main() {
		return "redirect:/login";
	}

	@GetMapping(value = "/user")
	public String userInfo(@AuthenticationPrincipal User user, Model model){
		model.addAttribute("user", user);
		model.addAttribute("roles", user.getRoles());
		return "userpage";
	}

	@GetMapping(value = "/admin")
	public String listUsers(Model model) {
		model.addAttribute("allUsers", userService.getAllUsers());
		return "adminpage";
	}

	@GetMapping(value = "/admin/new")
	public String newUser(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("roles", roleService.getAllRoles());
		return "new";
	}

	@PostMapping(value = "/admin/add-user")
	public String addUser(@ModelAttribute User user, @RequestParam(value = "checkBoxRoles") String[] checkBoxRoles) {
		Set<Role> roleSet = new HashSet<>();
		for (String role : checkBoxRoles) {
			roleSet.add(roleService.getRoleByName(role));
		}
		user.setRoles(roleSet);
		userService.addUser(user);
//        Set<Role> roleSet = Stream.of(checkBoxRoles).forEach();
//        user.setRoles(roleSet);
//        userService.addUser(user);

		return "redirect:/admin";
	}

	//страница для редактирования юзеров
	@GetMapping(value = "/edit/{id}")
	public String editUserForm(@PathVariable("id") long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("roles", roleService.getAllRoles());
		return "edit";
	}

	@PostMapping(value = "/edit")
	public String editUser(@ModelAttribute User user, @RequestParam(value = "checkBoxRoles") String[] checkBoxRoles) {
		Set<Role> roleSet = new HashSet<>();
		for (String roles : checkBoxRoles) {
			roleSet.add(roleService.getRoleByName(roles));
		}
		user.setRoles(roleSet);
		userService.updateUser(user);
		return "redirect:/admin";
	}

	@GetMapping(value = "/remove/{id}")
	public String removeUser(@PathVariable("id") long id) {
		userService.removeUserById(id);
		return "redirect:/admin";
	}
}