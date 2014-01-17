/*
  x = darwinCam(args);

  Author: Daniel D. Lee <ddlee@seas.upenn.edu>, 05/10
  	: Stephen McGill 10/10
*/

#include <string.h>
#include "v4l2.h"
#include "darwinCam.h"

#define VIDEO_DEVICE "/dev/video0"

/* Exposed C functions to Lua */
typedef unsigned char uint8;
typedef unsigned int uint32;

// Defined in v4l2.cc:
extern uint32 *bufArray;


int init = 0;

static int lua_get_select(lua_State *L){
  lua_pushinteger(L,0);
  return 1;
}

static int lua_get_height(lua_State *L){
  lua_pushinteger(L, v4l2_get_height());
  return 1;
}

static int lua_get_width(lua_State *L){
  
  lua_pushinteger(L, v4l2_get_width());
  return 1;
}


static int lua_get_image(lua_State *L) {
  int buf_num = v4l2_read_frame();
  if( buf_num < 0 ){
    lua_pushnumber(L,buf_num);
    return 1;
  }

  uint32* image = (uint32*)v4l2_get_buffer(buf_num, NULL);
  lua_pushlightuserdata(L, image);
  return 1;
}

static int lua_init(lua_State *L){
  int res = 1;
  v4l2_init( res );
  return 1;
}

static int lua_stop(lua_State *L){
  v4l2_close();
  return 1;
}

static int lua_stream_on(lua_State *L){
  v4l2_stream_on();
  return 1;
}

static int lua_stream_off(lua_State *L){
  v4l2_stream_off();
  return 1;
}

static int lua_set_param(lua_State *L) {
  const char *param = luaL_checkstring(L, 1);
  double value = luaL_checknumber(L, 2);

  int ret = v4l2_set_ctrl(param, value);
  lua_pushnumber(L, ret);

  return 1;
}

//Added
static int lua_set_param_id(lua_State *L) {
  double id = luaL_checknumber(L, 1);
  double value = luaL_checknumber(L, 2);

  int ret = v4l2_set_ctrl_by_id(id, value);
  lua_pushnumber(L, ret);

  return 1;
}

static int lua_get_param(lua_State *L) {
  const char *param = luaL_checkstring(L, 1);

  int value;
  double ret = v4l2_get_ctrl(param, &value);
  lua_pushnumber(L, value);

  return 1;
}


/* Lua Wrapper Requirements */
static const struct luaL_reg camera_lib [] = {
  {"get_image", lua_get_image},
  {"init", lua_init},
  {"stop",lua_stop},
  {"stream_on", lua_stream_on},
  {"stream_off", lua_stream_off},
  {"get_height", lua_get_height},
  {"get_width", lua_get_width},
  {"get_select", lua_get_select},
  {"set_param", lua_set_param},
  {"get_param", lua_get_param},
  {"set_param_id", lua_set_param_id},
  {NULL, NULL}
};

extern "C"
int luaopen_darwinCam (lua_State *L) {
  luaL_register(L, "camera", camera_lib);

  // Resolution = 1 means VGA (640x480)
  //int res = luaL_checkint(L, 2);
  int res = 1;

  if (!init) {
    if ( v4l2_open(VIDEO_DEVICE) == 0){
      init = 1;
      v4l2_init( res );
      v4l2_stream_on();
    }
  }
  
  return 1;
}

