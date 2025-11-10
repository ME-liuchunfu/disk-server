package com.spring.boot.disk.server.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

/**
 * description: 文件库
 *
 * @author spring
 * date 2025/11/10 11:58
 * version 1.0
 */
@Data
@TableName("`vm_disk_file`")
public class VmDiskFile {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Date statDate;

    private String hashValue;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String path;

    private String owner;

    private java.util.Date createTime;

}
