package service.actor;

import service.core.QuotationService;

public class Init {
    private QuotationService service;

    public Init(QuotationService service) {
        this.service = service;
    }

    public QuotationService getQuotationService() {
        return this.service;
    }
}
