package com.amnex.agristack.centralcore.entity;

import com.amnex.agristack.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
@Table(schema = "agristack_api_spec")
public class TransactionMaster extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionMasterId;

    @Column(columnDefinition = "TEXT")
    private String messageId;
    @Column(columnDefinition = "TEXT")
    private  String messageTs;

    @Column(columnDefinition = "TEXT")
    private  String senderId;

    @Column(columnDefinition = "TEXT")
    private  String senderUri;
    @Column(columnDefinition = "TEXT")
    private  String receiverId;
    private  int totalCount;
    private  Boolean isMsgEncrypted;
    @Column(columnDefinition = "TEXT",unique=true)
    private String transactionId;

    @Column(columnDefinition = "TEXT")
    private String ackStatus;

    @Column(columnDefinition = "TEXT",unique=true)
    private String  correlationId;



    @Column(columnDefinition = "TEXT")
    private String  locale;


    @Column(columnDefinition = "TEXT")
    private String regType;

    private String version;

    @Column(columnDefinition = "TEXT")
    private String mapperId;
    private Timestamp ackTimestamp;
    private String errorCode;
    private String errorMessage;
}
