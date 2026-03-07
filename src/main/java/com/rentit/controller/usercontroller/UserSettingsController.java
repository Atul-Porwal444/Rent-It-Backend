package com.rentit.controller.usercontroller;

import com.rentit.dto.UserSettingsDto;
import com.rentit.service.userprofileservice.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping("/setting")
    public ResponseEntity<UserSettingsDto> getSettings(Principal principal) {
        return ResponseEntity.ok(userSettingsService.getSettings(principal));
    }

    @PutMapping("/update")
    public ResponseEntity<UserSettingsDto> updateSettings(Principal principal, @RequestBody UserSettingsDto settings) {
        return ResponseEntity.ok(userSettingsService.updateSettings(principal, settings));
    }


}
