/**
 *
 */
package com.suixingpay.datas.manager.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.suixingpay.datas.manager.core.icon.MrNodeMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suixingpay.datas.common.statistics.TaskPerformance;
import com.suixingpay.datas.manager.core.entity.MrNodesMonitor;
import com.suixingpay.datas.manager.core.mapper.MrNodesMonitorMapper;
import com.suixingpay.datas.manager.core.util.DateFormatUtils;
import com.suixingpay.datas.manager.service.MrNodesMonitorService;
import com.suixingpay.datas.manager.web.page.Page;

/**
 * 节点任务实时监控表 服务实现类
 *
 * @author: FairyHood
 * @date: 2018-03-07 13:40:30
 * @version: V1.0-auto
 * @review: FairyHood/2018-03-07 13:40:30
 */
@Service
public class MrNodesMonitorServiceImpl implements MrNodesMonitorService {

    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>(128);

    @Autowired
    private MrNodesMonitorMapper mrNodesMonitorMapper;

    @Override
    public Integer insert(MrNodesMonitor mrNodesMonitor) {
        return mrNodesMonitorMapper.insert(mrNodesMonitor);
    }

    @Override
    public Integer update(Long id, MrNodesMonitor mrNodesMonitor) {
        return mrNodesMonitorMapper.update(id, mrNodesMonitor);
    }

    @Override
    public Integer delete(Long id) {
        return mrNodesMonitorMapper.delete(id);
    }

    @Override
    public MrNodesMonitor selectById(Long id) {
        return mrNodesMonitorMapper.selectById(id);
    }

    @Override
    public Page<MrNodesMonitor> page(Page<MrNodesMonitor> page) {
        Integer total = mrNodesMonitorMapper.pageAll(1);
        if (total > 0) {
            page.setTotalItems(total);
            page.setResult(mrNodesMonitorMapper.page(page, 1));
        }
        return page;
    }

    @Override
    public void dealTaskPerformance(TaskPerformance performance) {
        MrNodesMonitor mrNodesMonitor = new MrNodesMonitor(performance);
        String nodeId = mrNodesMonitor.getNodeId();
        String dataTimes = DateFormatUtils.formatDate("yyyy-MM-dd HH:mm:ss", mrNodesMonitor.getMonitorDate());
        String key = nodeId + dataTimes;
        Object lock = map.get(key);
        if (null == lock) {
            Object tmp = new Object();
            Object old = map.putIfAbsent(key, tmp);
            if (null != old) {
                lock = old;
            } else {
                lock = tmp;
            }
        }
        try {
            synchronized (lock) {
                dealTaskPerformanceSync(nodeId, dataTimes, mrNodesMonitor);
            }
        } finally {
            map.remove(key);
        }
    }

    @Override
    public MrNodeMonitor obNodeMonitor(String nodeId, Long intervalTime, Long intervalCount) {
        Long startRow = intervalTime * intervalCount;
        List<MrNodesMonitor> list = mrNodesMonitorMapper.selectByNodeId(nodeId, startRow, intervalTime);
        return new MrNodeMonitor(list);
    }

    private void dealTaskPerformanceSync(String nodeId, String dataTimes, MrNodesMonitor mrNodesMonitor) {
        MrNodesMonitor old = mrNodesMonitorMapper.selectByNodeIdAndTime(nodeId, dataTimes);
        if (old == null || old.getId() == null) {
            mrNodesMonitorMapper.insert(mrNodesMonitor);
        } else {
            mrNodesMonitor.setId(old.getId());
            mrNodesMonitor.setMonitorTps(mrNodesMonitor.getMonitorTps() + old.getMonitorTps());
            mrNodesMonitor.setMonitorAlarm(mrNodesMonitor.getMonitorAlarm() + old.getMonitorAlarm());
            mrNodesMonitorMapper.update(old.getId(), mrNodesMonitor);
        }
    }
}
