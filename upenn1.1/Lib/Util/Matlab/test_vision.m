global CAMERADATA

while 1
  figure(1);
  subplot(2,2,1);
  CAMERADATA.yuyv = dcmVision32('big_img');
  rgb=yuyv2rgb(CAMERADATA.yuyv);
  r=rgb([120:-1:1],[160:-1:1],1);
  g=rgb([120:-1:1],[160:-1:1],2);
  b=rgb([120:-1:1],[160:-1:1],3);
  rgb(:,:,1)=r;
  rgb(:,:,2)=g;
  rgb(:,:,3)=b;
  image(rgb);
  subplot(2,2,2);
  labelA = dcmVision8('big_labelA')';
  image( labelA([240:-1:1],[160:-1:1]));
  colormap([0,0,0;1 0 0]);

  CAMERADATA.headAngles = [];
  CAMERADATA.imuAngles = [];
  CAMERADATA.select = 0;

  %Logger;

  pause(.1);

end
