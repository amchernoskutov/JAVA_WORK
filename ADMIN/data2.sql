--
-- PostgreSQL database dump
--

-- Dumped from database version 11.4
-- Dumped by pg_dump version 12.2

-- Started on 2020-04-16 17:40:48

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

--
-- TOC entry 539 (class 1259 OID 1843459)
-- Name: settings; Type: TABLE; Schema: config; Owner: adm_db-ter
--

CREATE TABLE config.settings (
    id_system integer DEFAULT 0 NOT NULL,
    id_data integer DEFAULT 0 NOT NULL,
    code_module character(15) NOT NULL,
    property character varying NOT NULL,
    val character varying NOT NULL,
    annotation character varying DEFAULT ''::character varying
);


ALTER TABLE config.settings OWNER TO "adm_db-ter";

--
-- TOC entry 4676 (class 0 OID 0)
-- Dependencies: 539
-- Name: COLUMN settings.code_module; Type: COMMENT; Schema: config; Owner: adm_db-ter
--

COMMENT ON COLUMN config.settings.code_module IS 'Код модуля';


--
-- TOC entry 4670 (class 0 OID 1843459)
-- Dependencies: 539
-- Data for Name: settings; Type: TABLE DATA; Schema: config; Owner: adm_db-ter
--

COPY config.settings (id_system, id_data, code_module, property, val, annotation) FROM stdin;
0	0	LOADERSVLTR    	loadersvltr.logPath.1	samba/anonymous/dt/log1/	Ресурс для размещения log-файлов
0	0	LOADERSVLTR    	loadersvltr.logPath.2	samba/anonymous/dt/log2/	Ресурс для размещения log-файлов
0	0	LOADERSVLTR    	loadersvltr.ActiveMQRequestParam.url	tcp://10.81.253.245:61616	URL очереди для получения данных от АПК ЭЛЬБРУС
0	0	LOADERCOMM     	loadercomm.SOAPRequestParam.login	AS_ENERGO	Имя пользователя для подключения к SOAP сервису АС ЦОММ
0	0	LOADERCOMM     	loadercomm.logPath.1	samba/anonymous/dt/log1/	Ресурс для размещения log-файлов
0	0	LOADERSVLTR    	loadersvltr.ActiveMQRequestParam.queueName	80_mqdb_queue	Имя очереди для получения данных от АПК ЭЛЬБРУС
0	0	LOADERSVLTR    	loadersvltr.MQSenderParam.host	172.31.54.133	URL очереди для направления сообщения о загруженных данных
0	0	LOADERSVLTR    	loadersvltr.MQSenderParam.login	admin	Логин пользователя для направления сообщения в очередь о загруженных данных
0	0	LOADERSVLTR    	loadersvltr.MQSenderParam.name	q-test	Имя очереди для направления сообщения о загруженных данных
0	0	LOADERSVLTR    	loadersvltr.MQSenderParam.password	admin	Пароль пользователя для направления сообщения в очередь о загруженных данных
0	0	LOADERSVLTR    	loadersvltr.MQSenderParam.port	61613	Порт очереди для направления сообщения о загруженных данных
0	0	LOADERCOMM     	loadercomm.logPath.2	samba/anonymous/dt/log2/	Ресурс для размещения log-файлов
0	0	LOADERCOMM     	loadercomm.MQSenderParam.host	172.31.54.133	URL очереди для направления сообщения о загруженных данных
0	0	LOADERSVLTR    	loadersvltr.RestRequestParam.url	local.trans-ip.ru	URL REST сервиса СВЛ ТР
0	0	LOADERSVLTR    	loadersvltr.RestRequestParam.urlapi	ter/api/login	API REST сервиса СВЛ ТР
0	0	LOADERSVLTR    	loadersvltr.RestRequestParam.username	TERServiceBHbas2	Имя пользоваетля REST сервиса СВЛ ТР
0	0	LOADERSVLTR    	loadersvltr.RestRequestParam.password	JWNjgb3672bwVf!!m9	Пароль пользователя REST сервиса СВЛ ТР
0	201	LOADERSVLTR    	loadersvltr.request.1.urlapi	ter/api/speed_history	Функция получения данных от REST СВЛ ТР
0	201	LOADERSVLTR    	loadersvltr.request.1.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
1	1	DISPACHER      	xml_rootNode	DataMarshrut	Корневой внешний тег XML-файла
0	0	DISPACHER      	dbconnect.datasource	jdbc/postgresmain	jdbc/postgresmain
0	0	LOADERCOMM     	loadercomm.MQSenderParam.login	admin	Логин пользователя для направления сообщения в очередь о загруженных данных
0	0	LOADERCOMM     	loadercomm.MQSenderParam.name	q-asuter-mm	Имя очереди для направления сообщения о загруженных данных
0	0	LOADERCOMM     	loadercomm.MQSenderParam.password	admin	Пароль пользователя для направления сообщения в очередь о загруженных данных
0	0	LOADERCOMM     	loadercomm.MQSenderParam.port	61613	Порт очереди для направления сообщения о загруженных данных
0	1	LOADERCOMM     	loadercomm.request.1.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	1	LOADERCOMM     	loadercomm.request.1.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	2	LOADERCOMM     	loadercomm.request.2.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	2	LOADERCOMM     	loadercomm.request.2.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	3	LOADERCOMM     	loadercomm.request.3.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	3	LOADERCOMM     	loadercomm.request.3.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	4	LOADERCOMM     	loadercomm.request.4.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	4	LOADERCOMM     	loadercomm.request.4.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	5	LOADERCOMM     	loadercomm.request.5.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	5	LOADERCOMM     	loadercomm.request.5.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	6	LOADERCOMM     	loadercomm.request.6.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	6	LOADERCOMM     	loadercomm.request.6.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	0	LOADERCOMM     	loadercomm.SOAPRequestParam.password	OL6w1pDB	Пароль пользователя для подключения к SOAP сервису АС ЦОММ
0	0	LOADERCOMM     	loadercomm.SOAPRequestParam.soapActionNSI	Z63L_AS_EREPORT_GETNSI	Функция для получения НСИ от SOAP сервиса АС ЦОММ
0	0	LOADERCOMM     	loadercomm.SOAPRequestParam.soapActionOPER	Z63L_AS_EREPORT_GETOPER	Функция для получения операций от SOAP сервиса АС ЦОММ
0	0	LOADERCOMM     	loadercomm.SOAPRequestParam.url	http://verden.gvc.oao.rzd:8035/sap/bc/srt/rfc/sap/z63l_as_ereport/200/z63l_as_ereport/z63l_as_ereport	URL SOAP сервиса АС ЦОММ
0	201	LOADERSVLTR    	loadersvltr.request.1.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	202	LOADERSVLTR    	loadersvltr.request.2.urlapi	ter/api/coord_history	Функция получения данных от REST СВЛ ТР
0	202	LOADERSVLTR    	loadersvltr.request.2.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	202	LOADERSVLTR    	loadersvltr.request.2.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	203	LOADERSVLTR    	loadersvltr.request.3.urlapi	ter/api/traffic_light_history	Функция получения данных от REST СВЛ ТР
0	203	LOADERSVLTR    	loadersvltr.request.3.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	203	LOADERSVLTR    	loadersvltr.request.3.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	204	LOADERSVLTR    	loadersvltr.request.4.urlapi	ter/api/mek_history	Функция получения данных от REST СВЛ ТР
0	204	LOADERSVLTR    	loadersvltr.request.4.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	204	LOADERSVLTR    	loadersvltr.request.4.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	205	LOADERSVLTR    	loadersvltr.request.5.urlapi	ter/api/fuel	Функция получения данных от REST СВЛ ТР
0	10	LOADERCOMM     	loadercomm.request.10.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	10	LOADERCOMM     	loadercomm.request.10.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	7	LOADERCOMM     	loadercomm.request.7.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	7	LOADERCOMM     	loadercomm.request.7.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	8	LOADERCOMM     	loadercomm.request.8.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	8	LOADERCOMM     	loadercomm.request.8.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	9	LOADERCOMM     	loadercomm.request.9.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	9	LOADERCOMM     	loadercomm.request.9.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	0	LOADERELBRUS   	loaderelbrus.logPath.1	samba/anonymous/dt/log1/	Ресурс для размещения log-файлов
0	0	LOADERELBRUS   	loaderelbrus.logPath.2	samba/anonymous/dt/log2/	Ресурс для размещения log-файлов
0	0	LOADERELBRUS   	loaderelbrus.ActiveMQRequestParam.url	tcp://10.81.253.245:61616	URL очереди для получения данных от АПК ЭЛЬБРУС
0	0	LOADERELBRUS   	loaderelbrus.ActiveMQRequestParam.queueName	80_mqdb_queue	Имя очереди для получения данных от АПК ЭЛЬБРУС
0	0	LOADERELBRUS   	loaderelbrus.MQSenderParam.host	172.31.54.133	URL очереди для направления сообщения о загруженных данных
0	0	LOADERELBRUS   	loaderelbrus.MQSenderParam.login	admin	Логин пользователя для направления сообщения в очередь о загруженных данных
0	0	LOADERELBRUS   	loaderelbrus.MQSenderParam.name	q-test	Имя очереди для направления сообщения о загруженных данных
0	0	LOADERELBRUS   	loaderelbrus.MQSenderParam.password	admin	Пароль пользователя для направления сообщения в очередь о загруженных данных
0	0	LOADERELBRUS   	loaderelbrus.MQSenderParam.port	61613	Порт очереди для направления сообщения о загруженных данных
0	101	LOADERELBRUS   	loaderelbrus.request.1.timeout	120	Время ожидания ответа от очереди
0	101	LOADERELBRUS   	loaderelbrus.request.1.typeParam	NORMATIVE	Тип параметра для запроса к очереди
0	101	LOADERELBRUS   	loaderelbrus.request.1.userParam	GDP	Имя пользователя для запроса к очереди
0	101	LOADERELBRUS   	loaderelbrus.request.1.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	101	LOADERELBRUS   	loaderelbrus.request.1.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	102	LOADERELBRUS   	loaderelbrus.request.2.timeout	60	Время ожидания ответа от очереди
0	102	LOADERELBRUS   	loaderelbrus.request.2.typeParam	FORECASE	Тип параметра для запроса к очереди
0	102	LOADERELBRUS   	loaderelbrus.request.2.userParam	ELBRUS	Имя пользователя для запроса к очереди
0	102	LOADERELBRUS   	loaderelbrus.request.2.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	102	LOADERELBRUS   	loaderelbrus.request.2.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	103	LOADERELBRUS   	loaderelbrus.request.3.timeout	60	Время ожидания ответа от очереди
0	103	LOADERELBRUS   	loaderelbrus.request.3.typeParam	ACTUAL	Тип параметра для запроса к очереди
0	103	LOADERELBRUS   	loaderelbrus.request.3.userParam	ELBRUS	Имя пользователя для запроса к очереди
0	103	LOADERELBRUS   	loaderelbrus.request.3.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	103	LOADERELBRUS   	loaderelbrus.request.3.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	104	LOADERELBRUS   	loaderelbrus.request.4.timeout	60	Время ожидания ответа от очереди
0	104	LOADERELBRUS   	loaderelbrus.request.4.typeParam	ACTUAL	Тип параметра для запроса к очереди
0	104	LOADERELBRUS   	loaderelbrus.request.4.userParam	GID	Имя пользователя для запроса к очереди
0	104	LOADERELBRUS   	loaderelbrus.request.4.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	104	LOADERELBRUS   	loaderelbrus.request.4.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	205	LOADERSVLTR    	loadersvltr.request.5.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	205	LOADERSVLTR    	loadersvltr.request.5.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	206	LOADERSVLTR    	loadersvltr.request.6.urlapi	ter/api/diesel_run_history	Функция получения данных от REST СВЛ ТР
0	206	LOADERSVLTR    	loadersvltr.request.6.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	206	LOADERSVLTR    	loadersvltr.request.6.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	207	LOADERSVLTR    	loadersvltr.request.7.urlapi	ter/api/electric_data	Функция получения данных от REST СВЛ ТР
0	207	LOADERSVLTR    	loadersvltr.request.7.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	207	LOADERSVLTR    	loadersvltr.request.7.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	208	LOADERSVLTR    	loadersvltr.request.8.urlapi	ter/api/vagons_data	Функция получения данных от REST СВЛ ТР
0	208	LOADERSVLTR    	loadersvltr.request.8.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	208	LOADERSVLTR    	loadersvltr.request.8.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	209	LOADERSVLTR    	loadersvltr.request.9.urlapi	ter/api/asoup_data	Функция получения данных от REST СВЛ ТР
0	209	LOADERSVLTR    	loadersvltr.request.9.dataStartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	209	LOADERSVLTR    	loadersvltr.request.9.destPath	samba/anonymous/dt/data/	Ресурс для размещения файлов с данными
0	0	MAINSCHEDULER  	mainscheduler.logPath.1	samba/anonymous/dt/log1/	Ресурс для размещения log-файлов
0	0	MAINSCHEDULER  	mainscheduler.logPath.2	samba/anonymous/dt/log2/	Ресурс для размещения log-файлов
0	1	MAINSCHEDULER  	mainscheduler.request.1.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	1	MAINSCHEDULER  	mainscheduler.request.1.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	1	MAINSCHEDULER  	mainscheduler.request.1.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	2	MAINSCHEDULER  	mainscheduler.request.2.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	2	MAINSCHEDULER  	mainscheduler.request.2.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	2	MAINSCHEDULER  	mainscheduler.request.2.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	3	MAINSCHEDULER  	mainscheduler.request.3.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	3	MAINSCHEDULER  	mainscheduler.request.3.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	3	MAINSCHEDULER  	mainscheduler.request.3.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	4	MAINSCHEDULER  	mainscheduler.request.4.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	4	MAINSCHEDULER  	mainscheduler.request.4.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	4	MAINSCHEDULER  	mainscheduler.request.4.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	5	MAINSCHEDULER  	mainscheduler.request.5.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	5	MAINSCHEDULER  	mainscheduler.request.5.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	5	MAINSCHEDULER  	mainscheduler.request.5.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	6	MAINSCHEDULER  	mainscheduler.request.6.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	6	MAINSCHEDULER  	mainscheduler.request.6.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	6	MAINSCHEDULER  	mainscheduler.request.6.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	7	MAINSCHEDULER  	mainscheduler.request.7.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	7	MAINSCHEDULER  	mainscheduler.request.7.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	7	MAINSCHEDULER  	mainscheduler.request.7.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	8	MAINSCHEDULER  	mainscheduler.request.8.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	8	MAINSCHEDULER  	mainscheduler.request.8.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	8	MAINSCHEDULER  	mainscheduler.request.8.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	9	MAINSCHEDULER  	mainscheduler.request.9.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	9	MAINSCHEDULER  	mainscheduler.request.9.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	9	MAINSCHEDULER  	mainscheduler.request.9.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	10	MAINSCHEDULER  	mainscheduler.request.10.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	10	MAINSCHEDULER  	mainscheduler.request.10.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	10	MAINSCHEDULER  	mainscheduler.request.10.URLRESTService	http://172.31.54.39:8080/loadercomm/loadercommupload	URL REST сервиса запроса данных
0	101	MAINSCHEDULER  	mainscheduler.request.11.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	101	MAINSCHEDULER  	mainscheduler.request.11.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	101	MAINSCHEDULER  	mainscheduler.request.11.URLRESTService	http://172.31.54.39:8080/loaderelbrus/loaderelbrusupload	URL REST сервиса запроса данных
0	102	MAINSCHEDULER  	mainscheduler.request.12.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	102	MAINSCHEDULER  	mainscheduler.request.12.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	102	MAINSCHEDULER  	mainscheduler.request.12.URLRESTService	http://172.31.54.39:8080/loaderelbrus/loaderelbrusupload	URL REST сервиса запроса данных
0	103	MAINSCHEDULER  	mainscheduler.request.13.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	103	MAINSCHEDULER  	mainscheduler.request.13.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	103	MAINSCHEDULER  	mainscheduler.request.13.URLRESTService	http://172.31.54.39:8080/loaderelbrus/loaderelbrusupload	URL REST сервиса запроса данных
0	104	MAINSCHEDULER  	mainscheduler.request.14.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	104	MAINSCHEDULER  	mainscheduler.request.14.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	104	MAINSCHEDULER  	mainscheduler.request.14.URLRESTService	http://172.31.54.39:8080/loaderelbrus/loaderelbrusupload	URL REST сервиса запроса данных
0	201	MAINSCHEDULER  	mainscheduler.request.15.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	201	MAINSCHEDULER  	mainscheduler.request.15.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	201	MAINSCHEDULER  	mainscheduler.request.15.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	202	MAINSCHEDULER  	mainscheduler.request.16.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	202	MAINSCHEDULER  	mainscheduler.request.16.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	202	MAINSCHEDULER  	mainscheduler.request.16.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	203	MAINSCHEDULER  	mainscheduler.request.17.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	203	MAINSCHEDULER  	mainscheduler.request.17.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	203	MAINSCHEDULER  	mainscheduler.request.17.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	204	MAINSCHEDULER  	mainscheduler.request.18.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	204	MAINSCHEDULER  	mainscheduler.request.18.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	204	MAINSCHEDULER  	mainscheduler.request.18.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	205	MAINSCHEDULER  	mainscheduler.request.19.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	205	MAINSCHEDULER  	mainscheduler.request.19.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	205	MAINSCHEDULER  	mainscheduler.request.19.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	206	MAINSCHEDULER  	mainscheduler.request.20.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	206	MAINSCHEDULER  	mainscheduler.request.20.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	206	MAINSCHEDULER  	mainscheduler.request.20.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	207	MAINSCHEDULER  	mainscheduler.request.21.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	207	MAINSCHEDULER  	mainscheduler.request.21.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	207	MAINSCHEDULER  	mainscheduler.request.21.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	208	MAINSCHEDULER  	mainscheduler.request.22.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	208	MAINSCHEDULER  	mainscheduler.request.22.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	208	MAINSCHEDULER  	mainscheduler.request.22.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
0	209	MAINSCHEDULER  	mainscheduler.request.23.StartTime	2020-04-16 00:00:00	Дата с которой запрашивать данные
0	209	MAINSCHEDULER  	mainscheduler.request.23.intervalTimeMinute	1440	Интервал запроса данных в минутах
0	209	MAINSCHEDULER  	mainscheduler.request.23.URLRESTService	http://172.31.54.39:8080/loadersvltr/loadersvltrupload	URL REST сервиса запроса данных
1	1	DISPACHER      	xml_node	Marshrut	Внутренний тег XML-файла, который ограничевает одну запись маршрута
\.


--
-- TOC entry 4545 (class 2606 OID 1843662)
-- Name: settings settings_pk; Type: CONSTRAINT; Schema: config; Owner: adm_db-ter
--

ALTER TABLE ONLY config.settings
    ADD CONSTRAINT settings_pk PRIMARY KEY (code_module, property);


--
-- TOC entry 4546 (class 1259 OID 1843470)
-- Name: settings_property_idx; Type: INDEX; Schema: config; Owner: adm_db-ter
--

CREATE UNIQUE INDEX settings_property_idx ON config.settings USING btree (property);


-- Completed on 2020-04-16 17:40:48

--
-- PostgreSQL database dump complete
--

