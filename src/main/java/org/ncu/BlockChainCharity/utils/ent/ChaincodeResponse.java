package org.ncu.BlockChainCharity.utils.ent;

import ch.qos.logback.classic.turbo.MDCValueLevelPair;

public class ChaincodeResponse {
    public boolean getStatus() {
        return false;
    }

    public MDCValueLevelPair getResponse() {
        return null;
    }

    public class Status {
        public static final boolean SUCCESS = true;
    }
}
