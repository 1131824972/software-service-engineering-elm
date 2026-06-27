# JMeter Gateway API 测试摘要

测试时间：2026-06-23 19:59
测试工具：本机 D:\apache-jmeter-5.6.3\bin\jmeter.bat
测试方式：JMeter 5.6.3 非 GUI 模式
测试计划：elm-gateway-api-test-plan.jmx
线程数：10
循环次数：3
接口数量：7
总请求数：210
错误数：0
错误率：0.00%
平均响应时间：8.54 ms
最小响应时间：3 ms
最大响应时间：172 ms
结果文件：elm-gateway-api-results-20260623-195921.jtl

## 分接口结果

| 接口 | 样本数 | 错误数 | 平均响应时间(ms) |
| --- | ---: | ---: | ---: |
| 01_login | 30 | 0 | 10.90 |
| 02_categories | 30 | 0 | 8.47 |
| 03_business_detail | 30 | 0 | 11.43 |
| 04_foods | 30 | 0 | 6.57 |
| 05_cart_query | 30 | 0 | 6.70 |
| 06_address_query | 30 | 0 | 10.20 |
| 07_loadbalance_health | 30 | 0 | 5.50 |

结论：Gateway 路由下的核心 API 在 10 并发、3 轮循环下全部请求成功，错误率为 0.00%，可作为系统测试章节材料。