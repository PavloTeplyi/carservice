import java.math.BigDecimal;


public class CorporateCustomer extends Customer {

    private BigDecimal laborDiscountPercent;
    private BigDecimal partsDiscountPercent;

    public CorporateCustomer(Long id, String companyName, String phone,
                             BigDecimal laborDiscountPercent, BigDecimal partsDiscountPercent) {
        super(id, companyName, phone);
        setLaborDiscountPercent(laborDiscountPercent);
        setPartsDiscountPercent(partsDiscountPercent);
    }

    @Override
    public BigDecimal getLaborDiscountPercent() {
        return laborDiscountPercent;
    }

    public void setLaborDiscountPercent(BigDecimal laborDiscountPercent) {
        this.laborDiscountPercent = Validation.requirePercent(laborDiscountPercent, "Labor discount");
    }

    @Override
    public BigDecimal getPartsDiscountPercent() {
        return partsDiscountPercent;
    }

    public void setPartsDiscountPercent(BigDecimal partsDiscountPercent) {
        this.partsDiscountPercent = Validation.requirePercent(partsDiscountPercent, "Parts discount");
    }

    @Override
    public String toString() {
        return super.toString() + " [corporate, labor -" + laborDiscountPercent
                + "%, parts -" + partsDiscountPercent + "%]";
    }
}
