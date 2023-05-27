package cf.laptrinhweb.btl.controller.xacthuc;

import cf.laptrinhweb.btl.exception.xacthuc.MatKhauKhongDungException;
import cf.laptrinhweb.btl.service.XacThucService;
import cf.laptrinhweb.btl.service.impl.XacThucServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cf.laptrinhweb.btl.helper.HoTroXacThuc.yeuCauDangNhap;

@WebServlet("/doi-mat-khau")
public class DoiMatKhauController extends HttpServlet {
    private final XacThucService xacThucService = new XacThucServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        yeuCauDangNhap(req);
        req.getRequestDispatcher("/WEB-INF/doi_mat_khau.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        yeuCauDangNhap(req);
        try {
            // doi mat khau
            xacThucService.doiMatKhau(req);
            // dang xuat nguoi dung
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/dang-nhap");
        } catch (MatKhauKhongDungException e) {
            req.setAttribute("thongBao", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/doi_mat_khau.jsp").forward(req, resp);
        }
    }
}
