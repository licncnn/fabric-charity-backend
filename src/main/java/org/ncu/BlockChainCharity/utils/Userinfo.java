package org.ncu.BlockChainCharity.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Userinfo {
    private Long id;
    private String name;
    private String key="";
    private String avatar;
    private String introduction;
    private Object roles;
    private String type;
}
