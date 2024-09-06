package com.fsyy.ssetouch.touch.service;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TrafficService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * 保存到原始packets表，作为计算1分钟、10分钟、30分钟指标基础数据
     *
     * @param card
     * @param date
     * @param rx
     * @param tx
     */
    public void savePackets(String card, String date, Long rx, Long tx){
        int count = jdbcTemplate.update("update `net_card_packets` set `rx`=?, `tx`=? where `interface`=? and `date`=?", rx, tx, card, date);
        if(count == 0){
            jdbcTemplate.update("insert into `net_card_packets`(`interface`, `date`, `rx`, `tx`) values (?, ?, ?, ?)", card, date, rx, tx);
        }
    }

    /**
     * 获取网卡名
     *
     * @return
     */
    public List<Map<String, Object>> queryCards(){
        return jdbcTemplate.queryForList("select distinct interface as card from net_card_minute");
    }

    public List<Map<String, Object>> query(String card, Date startTime, Date endTime){
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select date_format(date, '%Y-%m-%d %H:%i') as date, interface as eth, `rx`, `tx`, `in`, `out`, `total`, `in-show` as inShow, `out-show` as outShow, `total-show` as totalShow, `inavg`, `outavg`, `allavg`, `inavg-show` as inavgShow, `outavg-show` as outavgShow, `allavg-show` as allavgShow from net_card_minute where interface=? and str_to_date(date, '%Y-%m-%d %H:%i') >= str_to_date(?, '%Y-%m-%d %H:%i') and str_to_date(date, '%Y-%m-%d %H:%i') <= str_to_date(?, '%Y-%m-%d %H:%i') order by `date` asc",
                card,
                DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm"),
                DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm")
                );
        return list;
    }
}
