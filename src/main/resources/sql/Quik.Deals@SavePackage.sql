CREATE OR REPLACE PROCEDURE "Quik"."Deals@SavePackage"(Data character varying)
    LANGUAGE PLPGSQL
AS $$
BEGIN
    INSERT INTO "Quik"."Deals"
    (
        "ExchangeCode",
        "TradeNum",
        "OrderNum",
        "BrokerRef",
        "UserId",
        "FirmId",
        "CanceledUid",
        "Account",
        "Price",
        "Quantity",
        "Value",
        "AccruedInterest",
        "Yield",
        "SettleCode",
        "CpFirmId",
        "Direction",
        "Price2",
        "RepoRate",
        "ClientCode",
        "Accrued2",
        "RepoTerm",
        "RepoValue",
        "Repo2Value",
        "StartDiscount",
        "LowerDiscount",
        "UpperDiscount",
        "BlockSecurities",
        "ClearingComission",
        "ExchangeComission",
        "TechCenterComission",
        "SettleDate",
        "SettleCurrency",
        "TradeCurrency",
        "StationId",
        "SecCode",
        "ClassCode",
        "TradeDateTime",
        "BankAccountId",
        "BrokerComission",
        "LinkedTrade",
        "Period",
        "TransactionId",
        "Kind",
        "ClearingBankAccountId",
        "CanceledDateTime",
        "ClearingFirmId",
        "SystemRef",
        "Uid"
    )
    SELECT D.*
    FROM JSONB_TO_RECORDSET(JSONB_EXTRACT_PATH(Data :: JSONB, '$.objects')) AS D
    (
        "exchangeCode"          character varying(255),
        "tradeNum"              character varying(50),
        "orderNum"              character varying(50),
        "brokerRef"             character varying(50),
        "userId"                character varying(50),
        "firmId"                character varying(50),
        "canceledUid"           bigint,
        "account"               character varying(50),
        "price"                 numeric(24,8),
        "quantity"              numeric(24,8),
        "value"                 numeric(24,8),
        "accruedInterest"       numeric(24,8),
        "yield"                 numeric(24,8),
        "settleCode"            character varying(50),
        "cpFirmId"              character varying(50),
        "direction"             character varying(10),
        "price2"                numeric(24,8),
        "repoRate"              numeric(24,8),
        "clientCode"            character varying(50),
        "accrued2"              numeric(24,8),
        "repoTerm"              integer,
        "repoValue"             numeric(24,8),
        "repo2Value"            numeric(24,8),
        "startDiscount"         numeric(24,8),
        "lowerDiscount"         numeric(24,8),
        "upperDiscount"         numeric(24,8),
        "blockSecurities"       numeric(24,8),
        "clearingComission"     numeric(24,8),
        "exchangeComission"     numeric(24,8),
        "techCenterComission"   numeric(24,8),
        "settleDate"            integer,
        "settleCurrency"        character varying(10),
        "tradeCurrency"         character varying(10),
        "stationId"             bigint,
        "secCode"               character varying(50),
        "classCode"             character varying(50),
        "tradeDateTime"         timestamp without time zone,
        "bankAccountId"         character varying(255),
        "brokerComission"       numeric(24,8),
        "linkedTrade"           bigint,
        "period"                smallint,
        "transactionId"         bigint,
        "kind"                  smallint,
        "clearingBankAccountId" character varying(255),
        "canceledDateTime"      timestamp without time zone,
        "clearingFirmId"        character varying(255),
        "systemRef"             character varying(255),
        "uid"                   bigint
    )
    ON CONFLICT ("ExchangeCode", "TradeNum") DO UPDATE SET
        "OrderNum"              = EXCLUDED."OrderNum",
        "BrokerRef"             = EXCLUDED."BrokerRef",
        "UserId"                = EXCLUDED."UserId",
        "FirmId"                = EXCLUDED."FirmId",
        "CanceledUid"           = EXCLUDED."CanceledUid",
        "Account"               = EXCLUDED."Account",
        "Price"                 = EXCLUDED."Price",
        "Quantity"              = EXCLUDED."Quantity",
        "Value"                 = EXCLUDED."Value",
        "AccruedInterest"       = EXCLUDED."AccruedInterest",
        "Yield"                 = EXCLUDED."Yield",
        "SettleCode"            = EXCLUDED."SettleCode",
        "CpFirmId"              = EXCLUDED."CpFirmId",
        "Direction"             = EXCLUDED."Direction",
        "Price2"                = EXCLUDED."Price2",
        "RepoRate"              = EXCLUDED."RepoRate",
        "ClientCode"            = EXCLUDED."ClientCode",
        "Accrued2"              = EXCLUDED."Accrued2",
        "RepoTerm"              = EXCLUDED."RepoTerm",
        "RepoValue"             = EXCLUDED."RepoValue",
        "Repo2Value"            = EXCLUDED."Repo2Value",
        "StartDiscount"         = EXCLUDED."StartDiscount",
        "LowerDiscount"         = EXCLUDED."LowerDiscount",
        "UpperDiscount"         = EXCLUDED."UpperDiscount",
        "BlockSecurities"       = EXCLUDED."BlockSecurities",
        "ClearingComission"     = EXCLUDED."ClearingComission",
        "ExchangeComission"     = EXCLUDED."ExchangeComission",
        "TechCenterComission"   = EXCLUDED."TechCenterComission",
        "SettleDate"            = EXCLUDED."SettleDate",
        "SettleCurrency"        = EXCLUDED."SettleCurrency",
        "TradeCurrency"         = EXCLUDED."TradeCurrency",
        "StationId"             = EXCLUDED."StationId",
        "SecCode"               = EXCLUDED."SecCode",
        "ClassCode"             = EXCLUDED."ClassCode",
        "TradeDateTime"         = EXCLUDED."TradeDateTime",
        "BankAccountId"         = EXCLUDED."BankAccountId",
        "BrokerComission"       = EXCLUDED."BrokerComission",
        "LinkedTrade"           = EXCLUDED."LinkedTrade",
        "Period"                = EXCLUDED."Period",
        "TransactionId"         = EXCLUDED."TransactionId",
        "Kind"                  = EXCLUDED."Kind",
        "ClearingBankAccountId" = EXCLUDED."ClearingBankAccountId",
        "CanceledDateTime"      = EXCLUDED."CanceledDateTime",
        "ClearingFirmId"        = EXCLUDED."ClearingFirmId",
        "SystemRef"             = EXCLUDED."SystemRef",
        "Uid"                   = EXCLUDED."Uid";
END;
$$;