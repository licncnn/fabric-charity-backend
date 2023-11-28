package org.ncu.BlockChainCharity.reqDto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadProjectDto {
    String projectName;
    String comment;
    double target;
    String materialPath;
    Long endTime;
}
