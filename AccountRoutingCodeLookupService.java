package com.airwallex.platform.services.payment.validation;


import com.airwallex.domain.transaction.RoutingType;
import com.airwallex.domain.transaction.payment.PaymentMethod;
import com.airwallex.platform.api.validation.RoutingCodeServiceRpc;
import com.airwallex.platform.common.validation.RoutingPair;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import com.neovisionaries.i18n.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AutoJsonRpcServiceImpl
public class AccountRoutingCodeLookupService implements RoutingCodeServiceRpc {

    public RoutingValues routingValues;

    @Autowired
    public AccountRoutingCodeLookupService(RoutingValues routingValues) {
        this.routingValues = routingValues;
    }

    @Override
    public boolean verifyRoutingCode(CountryCode country, RoutingType type, PaymentMethod method, String value) {
        int res;
        switch (country) {
            case HK:
                switch (type) {
                    case bank_code:
                        res = Collections.binarySearch(routingValues.getHK_BankCodes(), value);
                        return res >= 0;
                    case branch_code:
                        res = Collections.binarySearch(routingValues.getHK_BranchCode(), value);
                        return res >= 0;
                    default: return false;
                }
            case AU:
                switch (type) {
                    case bsb:
                        res = Collections.binarySearch(routingValues.getAU_BSBs(), value);
                        return res >= 0;

                    case bpay_biller_code:
                        // Put as placeholder since we don't get biller code list from BPay yet
                        res = Collections.binarySearch(routingValues.getAU_BPay_BillerCodes(), value);
                        return true;

                    default: return false;
                }
            case US:
                switch (type) {
                    case aba:
                        switch (method) {
                            case LOCAL:
                                res = Collections.binarySearch(routingValues.getUS_Local_ABAs(), value);
                                return res >= 0;
                            case SWIFT:
                                res = Collections.binarySearch(routingValues.getUS_Swift_ABAs(), value);
                                return res >= 0;
                            default: return false;
                        }
                    default: return false;
                }
            case CN:
                switch (type) {
                    case cnaps: return true;
                    default: return false;
                }
            default: return false;
        }
    }

    @Override
    public List<String> getRoutingCodes(CountryCode country, RoutingType type, PaymentMethod method) {
        switch (country) {
            case HK:
                switch (type) {
                    case bank_code:
                        return routingValues.getHK_BankCodes();
                    case branch_code:
                        return routingValues.getHK_BranchCode();
                    default: return null;
                }
            case AU:
                switch (type) {
                    case bsb:
                        return routingValues.getAU_BSBs();
                    case bpay_biller_code:
                        return routingValues.getAU_BPay_BillerCodes();
                    default:
                        return null;
                }
            case US:
                switch (type) {
                    case aba:
                        switch (method) {
                            case LOCAL:
                                return routingValues.getUS_Local_ABAs();
                            case SWIFT:
                                return routingValues.getUS_Swift_ABAs();
                            default:
                                return null;
                        }
                    default: return null;
                }
            case CN:
                switch (type) {
                    case cnaps:
                        return routingValues.getCN_CnapsCodes();
                    default:
                        return null;
                }
            default: return null;
        }
    }

    @Override
    public boolean verifyRoutingPair(CountryCode country, RoutingPair pair) {
        int res;
        switch (country) {
            case HK:
                res = Collections.binarySearch(routingValues.getHK_RouingCodes(), pair);
                return res >= 0;
            default:
                return false;
        }
    }

    @Override
    public List<RoutingPair> getRoutingPairs(CountryCode country) {
        switch (country) {
            case HK:
                return routingValues.getHK_RouingCodes();
            default:
                return null;
        }
    }


}

