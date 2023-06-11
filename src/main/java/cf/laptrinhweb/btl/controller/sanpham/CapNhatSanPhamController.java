package cf.laptrinhweb.btl.controller.sanpham;

import cf.laptrinhweb.btl.constant.QuyenNguoiDung;
import cf.laptrinhweb.btl.model.ThongTinSanPham;
import cf.laptrinhweb.btl.repository.ChatLieuRepository;
import cf.laptrinhweb.btl.repository.TheLoaiRepository;
import cf.laptrinhweb.btl.repository.ThuongHieuRepository;
import cf.laptrinhweb.btl.repository.impl.ChatLieuRepositoryImpl;
import cf.laptrinhweb.btl.repository.impl.TheLoaiRepositoryImpl;
import cf.laptrinhweb.btl.repository.impl.ThuongHieuRepositoryImpl;
import cf.laptrinhweb.btl.service.SanPhamService;
import cf.laptrinhweb.btl.service.impl.SanPhamServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cf.laptrinhweb.btl.helper.HoTroRequest.layThamSo;
import static cf.laptrinhweb.btl.helper.HoTroXacThuc.yeuCauQuyen;

@WebServlet("/quan-ly/san-pham/cap-nhat")
@MultipartConfig
public class CapNhatSanPhamController extends HttpServlet {
    private final SanPhamService sanPhamService = new SanPhamServiceImpl();
    private final TheLoaiRepository theLoaiRepository = new TheLoaiRepositoryImpl();
    private final ThuongHieuRepository thuongHieuRepository = new ThuongHieuRepositoryImpl();
    private final ChatLieuRepository chatLieuRepository = new ChatLieuRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        yeuCauQuyen(req, List.of(QuyenNguoiDung.QUAN_LY));
        Long idSanPham = Long.parseLong(layThamSo(req, "id"));
        ThongTinSanPham sanPham = sanPhamService.laySanPham(idSanPham);
        req.setAttribute("sanPham", sanPham);
        req.setAttribute("danhSachTheLoai", theLoaiRepository.layTatCa());
        req.setAttribute("danhSachThuongHieu", thuongHieuRepository.layTatCa());
        req.setAttribute("danhSachChatLieu", chatLieuRepository.layTatCa());
        req.getRequestDispatcher("/WEB-INF/form_cap_nhat_san_pham.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        yeuCauQuyen(req, List.of(QuyenNguoiDung.QUAN_LY));
        ThongTinSanPham sanPhamCapNhat = taoThongTinSanPham(req);
        Long idSanPham = Long.parseLong(layThamSo(req, "id"));
        List<Part> fileParts = req.getParts()
                .stream()
                .filter(part -> "anh".equalsIgnoreCase(part.getName()) && part.getSize() > 0)
                .toList();
        sanPhamService.capNhatSanPham(idSanPham, sanPhamCapNhat, fileParts);
        resp.sendRedirect(req.getContextPath() + "/quan-ly/san-pham/cap-nhat?id=" + idSanPham);
    }

    private ThongTinSanPham taoThongTinSanPham(HttpServletRequest request) {
        String kichThuoc = layThamSoKhongNull(request, "kichThuoc").isBlank()   
                ? null
                : request.getParameter("kichThuoc");
        Double trongLuong = layThamSoKhongNull(request, "trongLuong").isBlank()
                ? null
                : Double.parseDouble(layThamSoKhongNull(request, "trongLuong"));
        return ThongTinSanPham.builder()
                .ten(layThamSo(request, "tenSanPham"))
                .moTa(request.getParameter("moTa"))
                .maTheLoai(Long.parseLong(layThamSoKhongNull(request, "theLoai")))
                .gia(Double.parseDouble(layThamSoKhongNull(request, "gia")))
                .soLuong(Integer.parseInt
                