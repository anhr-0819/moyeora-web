package moyeora.myapp.dao;


import java.sql.Date;
import java.util.List;
import moyeora.myapp.dto.payment.RegularPaymentRequestDTO;
import moyeora.myapp.vo.BillingKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillingKeyDao {

  void add(RegularPaymentRequestDTO regularPaymentRequestDTO);

  List<BillingKey> findByDate(Date nowAt);

  void updateNextBillingDate(Date nextBillingDate, int no);
  void errorCountAdd(int no);
  List<BillingKey> findErrorCount();

  void deleteKey(int no);

  void deleteByError();

  void deleteByUserNo(int no);

  int findByUserNo(int no);

}
