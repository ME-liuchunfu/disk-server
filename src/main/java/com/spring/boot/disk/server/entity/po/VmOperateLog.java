package com.spring.boot.disk.server.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * description: 操作表
 *
 * @author spring
 * date 2025/11/10 12:00
 * version 1.0
 */
@Data
@TableName("`vm_operate_log`")
public class VmOperateLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String invokeMethod;

    private String invokeParams;

    private String invokeType;

    private String owner;

    private Date createTime;

}
