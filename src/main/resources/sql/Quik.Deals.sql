-- Table: Quik.Deals
-- DROP TABLE "Quik"."Deals";

CREATE TABLE IF NOT EXISTS "Quik"."Deals"
(
    "ExchangeCode"          character varying(255) COLLATE pg_catalog."default"     NOT NULL,
    "TradeNum"              character varying(50) COLLATE pg_catalog."default"      NOT NULL,
    "OrderNum"              character varying(50) COLLATE pg_catalog."default"          NULL,
    "BrokerRef"             character varying(50) COLLATE pg_catalog."default"          NULL,
    "UserId"                character varying(50) COLLATE pg_catalog."default"          NULL,
    "FirmId"                character varying(50) COLLATE pg_catalog."default"          NULL,
    "CanceledUid"           bigint                                                      NULL,
    "Account"               character varying(50) COLLATE pg_catalog."default"          NULL,
    "Price"                 numeric(24,8)                                               NULL,
    "Quantity"              numeric(24,8)                                           NOT NULL,
    "Value"                 numeric(24,8)                                           NOT NULL,
    "AccruedInterest"       numeric(24,8)                                               NULL,
    "Yield"                 numeric(24,8),
    "SettleCode"            character varying(50) COLLATE pg_catalog."default"          NULL,
    "CpFirmId"              character varying(50) COLLATE pg_catalog."default"          NULL,
    "Direction"             character varying(10) COLLATE pg_catalog."default"          NULL,
    "Price2"                numeric(24,8)                                               NULL,
    "RepoRate"              numeric(24,8)                                               NULL,
    "ClientCode"            character varying(50) COLLATE pg_catalog."default"          NULL,
    "Accrued2"              numeric(24,8)                                               NULL,
    "RepoTerm"              integer                                                     NULL,
    "RepoValue"             numeric(24,8)                                               NULL,
    "Repo2Value"            numeric(24,8)                                               NULL,
    "StartDiscount"         numeric(24,8)                                               NULL,
    "LowerDiscount"         numeric(24,8)                                               NULL,
    "UpperDiscount"         numeric(24,8)                                               NULL,
    "BlockSecurities"       numeric(24,8)                                               NULL,
    "ClearingComission"     numeric(24,8)                                               NULL,
    "ExchangeComission"     numeric(24,8)                                               NULL,
    "TechCenterComission"   numeric(24,8)                                               NULL,
    "SettleDate"            integer                                                     NULL,
    "SettleCurrency"        character varying(10) COLLATE pg_catalog."default"          NULL,
    "TradeCurrency"         character varying(10) COLLATE pg_catalog."default"          NULL,
    "StationId"             bigint                                                      NULL,
    "SecCode"               character varying(50) COLLATE pg_catalog."default"          NULL,
    "ClassCode"             character varying(50) COLLATE pg_catalog."default"          NULL,
    "TradeDateTime"         timestamp without time zone                             NOT NULL,
    "BankAccountId"         character varying(255) COLLATE pg_catalog."default"         NULL,
    "BrokerComission"       numeric(24,8)                                               NULL,
    "LinkedTrade"           bigint                                                      NULL,
    "Period"                smallint                                                    NULL,
    "TransactionId"         bigint                                                      NULL,
    "Kind"                  smallint                                                    NULL,
    "ClearingBankAccountId" character varying(255) COLLATE pg_catalog."default"         NULL,
    "CanceledDateTime"      timestamp without time zone                                 NULL,
    "ClearingFirmId"        character varying(255) COLLATE pg_catalog."default"         NULL,
    "SystemRef"             character varying(255) COLLATE pg_catalog."default"         NULL,
    "Uid"                   bigint                                                      NULL,

    CONSTRAINT "Deals_pkey" PRIMARY KEY ("ExchangeCode", "TradeNum")
) TABLESPACE pg_default;

ALTER TABLE "Quik"."Deals" OWNER TO quik;