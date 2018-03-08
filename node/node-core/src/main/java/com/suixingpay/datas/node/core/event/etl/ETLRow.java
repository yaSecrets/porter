/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月25日 18:41
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.node.core.event.etl;

import com.suixingpay.datas.node.core.event.s.EventType;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


/**
 *
 * 将消息源事件转换为ETLRow
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月25日 18:41
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月25日 18:41
 */
public class ETLRow {
    private final String schema;
    private final String table;
    private String finalSchema;
    private String finalTable;
    //操作类型 I U D T
    private final EventType opType;
    private final List<ETLColumn> columns;

    /**
     * 不包含在columns中
     * 源端没有，目标端必填且没有默认值的字段
     */
    private List<ETLColumn> additionalRequired= new ArrayList<>();

    //操作时间，保留该字段可以在需要的时候计算出与最终执行时间间隔
    private final Date opTime;

    //当前消息所在消费源的下标、顺序位置
    private final String position;

    /**
     * 扩展字段，不同的载入器插件有不同的值
     */
    private Map<String, Object> extendsField = new LinkedHashMap<>();

    public ETLRow(String schema, String table, EventType opType, List<ETLColumn> columns, Date opTime, String position) {
        this.schema = schema;
        this.table = table;
        this.opType = opType;
        this.columns = columns;
        this.opTime = opTime;

        //数据映射时使用
        this.finalSchema = schema;
        this.finalTable = table;
        this.position = position;
    }
    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public EventType getOpType() {
        return opType;
    }

    public List<ETLColumn> getColumns() {
        return columns;
    }

    public Date getOpTime() {
        return opTime;
    }

    public String getFinalSchema() {
        return finalSchema;
    }

    public void setFinalSchema(String finalSchema) {
        this.finalSchema = finalSchema;
    }

    public String getFinalTable() {
        return finalTable;
    }

    public void setFinalTable(String finalTable) {
        this.finalTable = finalTable;
    }

    public String getPosition() {
        return position;
    }

    public List<ETLColumn> getAdditionalRequired() {
        return additionalRequired;
    }

    public void setAdditionalRequired(List<ETLColumn> additionalRequired) {
        this.additionalRequired = additionalRequired;
    }

    public Map<String, Object> getExtendsField() {
        return extendsField;
    }
}
