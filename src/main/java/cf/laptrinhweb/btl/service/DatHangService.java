package cf.laptrinhweb.btl.service;
import cf.laptrinhweb.btl.entity.*;
import java.util.List;

public interface DatHangService {
    void themDatHang(DatHang dathang, List<SanPhamTrongGio> listGio, NguoiDung nguoidung);
    List<DatHang> layTatCaCuaNguoiDung(NguoiDung nguoidung);
    DatHang layDonTheoMaDatHang(Long maDatHang, NguoiDung nguoidung);
}
