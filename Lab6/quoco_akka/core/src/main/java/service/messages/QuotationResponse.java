package service.messages;
import service.core.Quotation;

public class QuotationResponse implements MyInterface {
    private int id;
    private Quotation quotation;
    public QuotationResponse(int id, Quotation quotation) {
        this.id = id;
        this.quotation = quotation;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }
} 
