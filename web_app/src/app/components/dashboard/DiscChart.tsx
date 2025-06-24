"use client";

import { PureComponent } from 'react';
import { PieChart, Pie, Sector, ResponsiveContainer } from 'recharts';
import styles from '@/app/components/dashboard/ChartStyle.module.scss';
import type { JSX } from 'react';


interface Props {
  data: {
    name: string;
    value: number;
    fill: string;
  }[];
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const renderActiveShape = (props: any): JSX.Element => {
  const RADIAN = Math.PI / 180;
  const { cx, cy, midAngle, innerRadius, outerRadius, startAngle, endAngle, fill, payload, percent, value } = props;
  const sin = Math.sin(-RADIAN * midAngle);
  const cos = Math.cos(-RADIAN * midAngle);
  const sx = cx + (outerRadius + 10) * cos;
  const sy = cy + (outerRadius + 10) * sin;
  const mx = cx + (outerRadius + 30) * cos;
  const my = cy + (outerRadius + 30) * sin;
  const ex = mx + (cos >= 0 ? 1 : -1) * 22;
  const ey = my;
  const textAnchor = cos >= 0 ? 'start' : 'end';

  return (
    <g>
      <text x={cx} y={cy} dy={8} textAnchor="middle" fill={fill}>
        {payload.name}
      </text>
      <Sector cx={cx} cy={cy} innerRadius={innerRadius} outerRadius={outerRadius} startAngle={startAngle} endAngle={endAngle} fill={fill} />
      <Sector cx={cx} cy={cy} startAngle={startAngle} endAngle={endAngle} innerRadius={outerRadius + 6} outerRadius={outerRadius + 10} fill={fill} />
      <path d={`M${sx},${sy}L${mx},${my}L${ex},${ey}`} stroke={fill} fill="none" />
      <circle cx={ex} cy={ey} r={2} fill={fill} stroke="none" />
      <text x={ex + (cos >= 0 ? 1 : -1) * 12} y={ey} textAnchor={textAnchor} fill="#f4f3ee">{`${value} GB`}</text>
      <text x={ex + (cos >= 0 ? 1 : -1) * 12} y={ey} dy={18} textAnchor={textAnchor} fill="#999">{`${(percent * 100).toFixed(2)}%`}</text>
    </g>
  );
};

export default class DiscChart extends PureComponent<Props> {
  state = {
    activeIndex: 0,
  };

  onPieEnter = (_: unknown, index: number) => {
    this.setState({ activeIndex: index });
  };


  render() {
    const data = this.props.data || [];
    const total = data.reduce((sum, entry) => sum + entry.value, 0);

    return (
      <div className={styles.container}>
        <div className={styles.chartWrapper}>
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                activeIndex={this.state.activeIndex}
                activeShape={renderActiveShape}
                data={data}
                cx="50%"
                cy="50%"
                innerRadius="40%"
                outerRadius="60%"
                dataKey="value"
                onMouseEnter={this.onPieEnter}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <ul className={styles.legend}>
          {data.map(({ name, value }, index) => {
            const percent = ((value / total) * 100).toFixed(2);
            const color = data[index].fill || '#8884d8';
            return (
              <li key={name}>
                <span style={{ backgroundColor: color }} />
                {name}: {value} GB ({percent}%)
              </li>
            );
          })}
        </ul>
      </div>
    );
  }
}
