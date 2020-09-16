package com.avril.framework.manager.factory;

import com.avril.common.constant.Constants;
import com.avril.common.utils.AddressUtils;
import com.avril.common.utils.LogUtils;
import com.avril.common.utils.ServletUtils;
import com.avril.common.utils.StringUtils;
import com.avril.common.utils.security.ShiroUtils;
import com.avril.common.utils.spring.SpringUtils;
import com.avril.framework.consolelog.ConsoleLog;
import com.avril.framework.consolelog.ConsoleLogQueue;
import com.avril.project.monitor.logininfor.domain.Logininfor;
import com.avril.project.monitor.logininfor.service.LogininforServiceImpl;
import com.avril.project.monitor.online.domain.OnlineSession;
import com.avril.project.monitor.online.domain.UserOnline;
import com.avril.project.monitor.online.service.IUserOnlineService;
import com.avril.project.monitor.operlog.domain.OperLog;
import com.avril.project.monitor.operlog.service.IOperLogService;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 * 
 * @author liuhulu
 *
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    private static Logger logger = LoggerFactory.getLogger(AsyncFactory.class);

    /**
     * 同步session到数据库
     * 
     * @param session 在线用户会话
     * @return 任务task
     */
    public static TimerTask syncSessionToDb(final OnlineSession session)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                UserOnline online = new UserOnline();
                online.setSessionId(String.valueOf(session.getId()));
                online.setDeptName(session.getDeptName());
                online.setLoginName(session.getLoginName());
                online.setStartTimestamp(session.getStartTimestamp());
                online.setLastAccessTime(session.getLastAccessTime());
                online.setExpireTime(session.getTimeout());
                online.setIpaddr(session.getHost());
                online.setLoginLocation(AddressUtils.getRealAddressByIP(session.getHost()));
                online.setBrowser(session.getBrowser());
                online.setOs(session.getOs());
                online.setStatus(session.getStatus());
                online.setSession(session);
                SpringUtils.getBean(IUserOnlineService.class).saveOnline(online);

            }
        };
    }

    /**
     * 操作日志记录
     * 
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final OperLog operLog)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(IOperLogService.class).insertOperlog(operLog);
            }
        };
    }

    /**
     * 记录登陆信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message, final Object... args)
    {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = ShiroUtils.getIp();
        return new TimerTask()
        {
            @Override
            public void run()
            {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                Logininfor logininfor = new Logininfor();
                logininfor.setLoginName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                // 日志状态
                if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER))
                {
                    logininfor.setStatus(Constants.SUCCESS);
                }
                else if (Constants.LOGIN_FAIL.equals(status))
                {
                    logininfor.setStatus(Constants.FAIL);
                }
                // 插入数据
                SpringUtils.getBean(LogininforServiceImpl.class).insertLogininfor(logininfor);
            }
        };
    }

    /**
     * 推送日志到/topic/pullLogger任务
     *
     * @return 任务task
     */
    public static TimerTask pushLogger()
    {
        return new TimerTask()
        {
            @Autowired
            private SimpMessagingTemplate messagingTemplate;

            @Override
            public void run()
            {
                for (; ; ) {
                    try {
                        ConsoleLog log = ConsoleLogQueue.getInstance().poll();
                        if (log != null && messagingTemplate != null) {
                            logger.info("推送日志:{}", log);
                            messagingTemplate.convertAndSend("/topic/consolelog", log);
                        }
                    } catch (Exception e) {
                        logger.warn("推送日志失败:{}", e.getMessage());
                    }
                }
            }
        };
    }
}
