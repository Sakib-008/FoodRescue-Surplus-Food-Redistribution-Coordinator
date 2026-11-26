package com.foodrescue.admin.controller;

import com.foodrescue.admin.dto.AdminDtos.CreateUserRequest;
import com.foodrescue.admin.dto.AdminDtos.RoleRequest;
import com.foodrescue.admin.dto.AdminDtos.StatusRequest;
import com.foodrescue.admin.dto.AdminDtos.UpdateUserRequest;
import com.foodrescue.admin.report.ExportableReport;
import com.foodrescue.admin.service.AdminService;
import com.foodrescue.admin.service.ReportService;
import com.foodrescue.admin.service.ReportService.RescueReport;
import com.foodrescue.common.response.ApiResponse;
import com.foodrescue.common.response.PageResponse;
import com.foodrescue.security.context.CurrentUser;
import com.foodrescue.user.dto.UserDto;
import com.foodrescue.user.entity.Role;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** ADMIN only (URL rule in SecurityConfig + @PreAuthorize): user management and dashboard statistics. */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    @GetMapping("/users")
    public ApiResponse<PageResponse<UserDto>> users(@RequestParam(required = false) Role role,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(adminService.listUsers(role, page, size));
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserDto> user(@PathVariable Long id) {
        return ApiResponse.ok(adminService.getUser(id));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDto>> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(adminService.createUser(req)));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<UserDto> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return ApiResponse.ok(adminService.updateUser(id, req));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.deleteUser(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/status")
    public ApiResponse<UserDto> status(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return ApiResponse.ok(adminService.setEnabled(CurrentUser.id(), id, req.enabled()));
    }

    @PatchMapping("/users/{id}/role")
    public ApiResponse<UserDto> role(@PathVariable Long id, @Valid @RequestBody RoleRequest req) {
        return ApiResponse.ok(adminService.setRole(CurrentUser.id(), id, req.role()));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.ok(reportService.dashboardStats());
    }

    @GetMapping("/reports/rescued")
    public ApiResponse<RescueReport> rescued() {
        return ApiResponse.ok(reportService.rescueReport());
    }

    /** Download a report as a file: /api/admin/reports/rescued-food/export?format=csv (Bridge pattern). */
    @GetMapping("/reports/{report}/export")
    public ResponseEntity<String> export(@PathVariable String report, @RequestParam(defaultValue = "csv") String format) {
        ExportableReport exportable = reportService.export(report, format);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportable.fileName() + "\"")
                .contentType(MediaType.parseMediaType(exportable.contentType() + ";charset=UTF-8"))
                .body(exportable.export());
    }
}
