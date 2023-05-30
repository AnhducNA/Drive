package com.kma.drive;

public class SanPham {
    private String tenSanPham;
    private int hinhSanPham;

    public SanPham(String tenSanPham, int hinhSanPham){
        this.tenSanPham=tenSanPham;
        this.hinhSanPham=hinhSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public int getHinhSanPham() {
        return hinhSanPham;
    }

    public void setHinhSanPham(int hinhSanPham) {
        this.hinhSanPham = hinhSanPham;
    }
}
