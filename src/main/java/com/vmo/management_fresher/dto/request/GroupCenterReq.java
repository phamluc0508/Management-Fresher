package com.vmo.management_fresher.dto.request;

import java.util.List;

import com.vmo.management_fresher.model.Center;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCenterReq {
    private List<Long> sourceCenterIds;
    private Long destinationCenterId;
    private Center newCenter;
}
