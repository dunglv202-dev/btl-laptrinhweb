package cf.laptrinhweb.btl.service.impl;

import cf.laptrinhweb.btl.constant.LoaiThongTinDangNhap;
import cf.laptrinhweb.btl.exception.xacthuc.ThongTinDangNhapDaTonTaiException;
import cf.laptrinhweb.btl.exception.chung.ThongTinKhongHopLeException;
import cf.laptrinhweb.btl.exception.xacthuc.SaiThongTinDangNhapException;
import cf.laptrinhweb.btl.model.NguoiDung;
import cf.laptrinhweb.btl.model.Quyen;
import cf.laptrinhweb.btl.repository.NguoiDungRepository;
import cf.laptrinhweb.btl.repository.PhanQuyenRepository;
import cf.laptrinhweb.btl.repository.QuyenRepository;
import cf.laptrinhweb.btl.repository.impl.NguoiDungRepositoryImpl;
import cf.laptrinhweb.btl.repository.impl.PhanQuyenRepositoryImpl;
import cf.laptrinhweb.btl.repository.impl.QuyenRepositoryImpl;
import cf.laptrinhweb.btl.service.XacThucService;

import java.util.*;

import static cf.laptrinhweb.btl.constant.Patterns.*;
import static cf.laptrinhweb.btl.constant.QuyenNguoiDung.KHACH_HANG;

public class XacThucServiceImpl implements XacThucService {
    private final NguoiDungRepository nguoiDungRepository = new NguoiDungRepositoryImpl();
    private final QuyenRepository quyenRepository = new QuyenRepositoryImpl();
    private final PhanQuyenRepository phanQuyenRepository = new PhanQuyenRepositoryImpl();

    @Override
    public void dangKy(Map<String, String[]> thongTinDangKy) {
        NguoiDung nguoiDung = taoNguoiDung(thongTinDangKy);
        kiemTraThongTin(nguoiDung);
        nguoiDungRepository.taoMoiNguoiDung(nguoiDung);
        themQuyenChoNguoiDung(nguoiDung, Set.of(KHACH_HANG));
    }

    private void kiemTraThongTin(NguoiDung nguoiDung) {
        if (nguoiDung.getTenHienThi().isBlank()
            || !nguoiDung.getTenDangNhap().matches(TEN_DANG_NHAP.pattern())
            || !nguoiDung.getMatKhau().matches(MAT_KHAU.pattern())
            || !nguoiDung.getEmail().matches(EMAIL.pattern())
            || !nguoiDung.getSoDienThoai().matches(SO_DIEN_THOAI.pattern())) {
            throw new ThongTinKhongHopLeException();
        }
        Set<LoaiThongTinDangNhap> thongTinTrungLap = new HashSet<>();
        if (nguoiDungRepository.tonTaiVoiTenDangNhap(nguoiDung.getTenDangNhap()))
            thongTinTrungLap.add(LoaiThongTinDangNhap.TEN_DANG_NHAP);
        if (nguoiDungRepository.tonTaiVoiEmail(nguoiDung.getEmail()))
            thongTinTrungLap.add(LoaiThongTinDangNhap.EMAIL);
        if (nguoiDungRepository.tonTaiVoiSoDienThoai(nguoiDung.getSoDienThoai()))
            thongTinTrungLap.add(LoaiThongTinDangNhap.SO_DIEN_THOAI);
        if (!thongTinTrungLap.isEmpty())
            throw new ThongTinDangNhapDaTonTaiException(thongTinTrungLap);
    }

    private NguoiDung taoNguoiDung(Map<String, String[]> thongTinDangKy) {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTenDangNhap(thongTinDangKy.get("tenDangNhap")[0]);
        nguoiDung.setMatKhau(thongTinDangKy.get("matKhau")[0]);
        nguoiDung.setEmail(thongTinDangKy.get("email")[0]);
        nguoiDung.setSoDienThoai(thongTinDangKy.get("soDienThoai")[0]);
        nguoiDung.setTenHienThi(thongTinDangKy.get("ten")[0]);
        return nguoiDung;
    }

    @Override
    public NguoiDung dangNhap(String tenDangNhap, String matKhau) {
        Objects.requireNonNull(tenDangNhap, "Tên đăng nhập không được nhận giá trị null");
    	NguoiDung nguoiDung = nguoiDungRepository.timBangThongTinDangNhap(tenDangNhap)
            .orElseThrow(SaiThongTinDangNhapException::new);
        if (!nguoiDung.getMatKhau().equals(matKhau))
            throw new SaiThongTinDangNhapException();
        List<Quyen> dsQuyen = phanQuyenRepository.timBangMaNguoiDung(nguoiDung.getMaNguoiDung());
        nguoiDung.setDsQuyen(dsQuyen);
        return nguoiDung;
    }

    private void themQuyenChoNguoiDung(NguoiDung nguoiDung, Set<String> quyenDuocPhan) {
        List<Long> danhSachQuyen = new ArrayList<>();
        quyenDuocPhan.forEach(tenQuyen -> {
            Quyen quyen = quyenRepository.timBangTen(tenQuyen)
                    .orElseThrow(() -> new RuntimeException("Quyen khong ton tai : " + tenQuyen));
            danhSachQuyen.add(quyen.getMaQuyen());
        });
        phanQuyenRepository.themQuyenChoNguoiDung(nguoiDung.getMaNguoiDung(), danhSachQuyen);
    }
}
