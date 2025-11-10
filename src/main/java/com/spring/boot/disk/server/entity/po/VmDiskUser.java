package com.spring.boot.disk.server.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * description: 用户信息表
 *
 * @author spring
 * date 2025/11/10 11:55
 * version 1.0
 */
@Data
@TableName("`vm_disk_user`")
public class VmDiskUser {

    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    private String userName;

    private String userNick;

    private String password;

    private String email;

    private Integer acStatus;

    private Date createTime;

}
