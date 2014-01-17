function rgb=yuyv2rgb(yuyv);

siz = size(yuyv);
yuyv_u8 = reshape(typecast(yuyv(:), 'uint8'), [4 siz]);

ycbcr = yuyv_u8([1 2 4], :, 1:2:end); % This should give the yuv of one set

ycbcr = permute(ycbcr, [3 2 1]);
rgb = ycbcr2rgb(ycbcr);
