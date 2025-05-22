package com.ssafyhome.community.reply.dto;

import lombok.Getter;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class ReplyDetailDto {
    private int replyIdx;
    private String replyContent;
    private String replyRegDate;
    private String username;
    private String userEmail;
    private byte[] profile;

    public ReplyDetailDto(int replyIdx, String replyContent, Date replyRegDate, String username, String userEmail, byte[] profile) {
        this.replyIdx = replyIdx;
        this.replyContent = replyContent;
        this.replyRegDate = formatDate(replyRegDate);
        this.username = username;
        this.userEmail = userEmail;
        this.profile = profile;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}
