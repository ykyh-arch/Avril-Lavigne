package com.avril.project.monitor.log.controller;

import com.avril.framework.consolelog.ConsoleLog;
import com.avril.framework.web.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 日志监控
 *
 * @author King
 */
@Controller
@RequestMapping("/monitor/consolelog")
public class ConsoleLogController extends BaseController {

    private final String prefix = "monitor/consolelog";

    @RequiresPermissions(ConsoleLog.VIEW_PERM)
    @GetMapping
    public String server() {
        return prefix + "/log";
    }
}
