package cf.laptrinhweb.btl.repository;
import cf.laptrinhweb.btl.entity.SanPhamDat;
import java.util.*;

public interface SanPhamDatRepository extends JdbcRepository{
	void themSanPhamDat(SanPhamDat sanpham);
}
