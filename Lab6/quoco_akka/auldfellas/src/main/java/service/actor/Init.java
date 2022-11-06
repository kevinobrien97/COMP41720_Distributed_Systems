package service.actor;

import service.core.QuotationService;

public class Init {
    private QuotationService service;
    // when instance of this class is instantiated it is passed a quotation service
    public Init(QuotationService service) {
        this.service = service;
    }

    // return the instance of the quotation service passed when instantiated
    public QuotationService getQuotationService() {
        return this.service;
    }
}
