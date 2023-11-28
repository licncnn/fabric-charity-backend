package org.ncu.BlockChainCharity.utils.ent;

import java.util.List;

public class Block {

    public List<Block.TransactionEnvelopeInfo> getTransactionEnvelopeInfos() {
        return null;
    }

    public class TransactionEnvelopeInfo {
        public boolean getType() {
            return false;
        }

        public TransactionActionInfo getTransactionActionInfo() {
            return null;
        }

        public class Type {
            public static final boolean TRANSACTION_ENVELOPE = true;
        }

        public class TransactionActionInfo {
            public String getTransactionId() {
                return null;
            }
        }
    }
}
