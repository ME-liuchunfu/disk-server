package com.spring.boot.disk.server.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * description: 用户文件
 *
 * @author spring
 * date 2025/11/10 11:28
 * version 1.0
 */
@Data
@TableName("`vm_user_file`")
public class VmUserFile {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;

    private String title;

    private Long refFileId;

    private Long refAvatarFileId;

    private Integer folder;

    private Date createTime;

    private Long owner;

}
