package cn.edu.cqwu.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long toUserId;
    private String message;
}
