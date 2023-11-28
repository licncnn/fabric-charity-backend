package org;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ncu.BlockChainCharity.dto.BlockchainDTO;
import org.ncu.BlockChainCharity.service.WithDrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WithDrawServiceTest {

    @Autowired
    private WithDrawService withDrawService;

    @Test
    public void getAllWithDrawRecords() throws Exception {
        List<Long> times = new ArrayList<>();
        for(int i=0; i<100; i++) {
            long start = System.currentTimeMillis();
            BlockchainDTO result = withDrawService.getAllWithDrawRecords();
            long end = System.currentTimeMillis();
            times.add(end - start);
        }
        long totalTime = 0;
        for(long time : times) {
            totalTime += time;
        }
        System.out.println(" getAllWithDrawRecords方法100次调用的平均时间:" + totalTime/100 + "ms");
    }
}