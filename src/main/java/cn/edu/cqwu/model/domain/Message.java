package cn.edu.cqwu.model.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送消息方用户id
     */
    private Long fromUserId;

    /**
     * 接收消息方id
     */
    private Long toUserId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 发送消息时间
     */
    private Date createTime;

    /**
     * 0 - 未删除
     * 1 - 已删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}